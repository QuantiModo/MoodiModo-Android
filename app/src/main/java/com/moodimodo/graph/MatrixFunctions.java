/***************************************************************************
 *   Copyright (C) 2012 by Paul Lutus                                      *
 *   lutusp@arachnoid.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
package com.moodimodo.graph;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @author lutusp
 */
public final class MatrixFunctions
{

	// don't allow this class to be instantiated
	private MatrixFunctions()
	{
	}

	// classic Gauss-Jordan matrix manipulation functions
	static private void gj_divide(double[][] A, int i, int j, int m)
	{
		for (int q = j + 1; q < m; q++)
		{
			A[i][q] /= A[i][j];
		}
		A[i][j] = 1;
	}

	static private void gj_eliminate(double[][] A, int i, int j, int n, int m)
	{
		for (int k = 0; k < n; k++)
		{
			if (k != i && A[k][j] != 0)
			{
				for (int q = j + 1; q < m; q++)
				{
					A[k][q] -= A[k][j] * A[i][q];
				}
				A[k][j] = 0;
			}
		}
	}

	static private void gj_echelonize(double[][] A)
	{
		int n = A.length;
		int m = A[0].length;
		int i = 0;
		int j = 0;
		int k;
		double[] swap;
		while (i < n && j < m)
		{
			//look for non-zero entries in col j at or below row i
			k = i;
			while (k < n && A[k][j] == 0)
			{
				k++;
			}
			// if an entry is found at row k
			if (k < n)
			{
				//  if k is not i, then swap row i with row k
				if (k != i)
				{
					swap = A[i];
					A[i] = A[k];
					A[k] = swap;
				}
				// if A[i][j] is != 1, divide row i by A[i][j]
				if (A[i][j] != 1)
				{
					gj_divide(A, i, j, m);
				}
				// eliminate all other non-zero entries
				gj_eliminate(A, i, j, n, m);
				i++;
			}
			j++;
		}
	}

	// produce a single y result for a given x
	public static double regress(double x, ArrayList<Double> terms)
	{
		double a = 0;
		int exp = 0;
		for (double term : terms)
		{
			a += term * Math.pow(x, exp);
			exp++;
		}
		return a;
	}



	/**
	 * Calculate smooth data from graph, using regression to determine Y. Modifies input data object
	 * @param data Data would be modified
     * @return
     */
	public static ArrayList<Map.Entry<Date,Double>> smoothData(ArrayList<Map.Entry<Date,Double>> data,long min, long max){
		int polyOrder = Math.max(5, data.size() / 12);
		ArrayList<Double> terms = computeCoefficients(data,polyOrder,min,max);

		for(Map.Entry<Date,Double> e : data){
			e.setValue(regress(normalize(e.getKey().getTime() / 1000,min,max),terms));
		}

		return data;
	}

	private static double normalize(long value, final long min, final long max) {
		return 2.0 * ((double)(value - min) / (max - min)) - 1.0;
	}

	private static ArrayList<Double> computeCoefficients(ArrayList<Map.Entry<Date, Double>> data, int p,long min,long max) {
		p += 1;
		int n = data.size();
		int r, c;
		int rs = 2 * p - 1;
		//
		// by request: read each datum only once
		// not the most efficient processing method
		// but required if the data set is huge
		//
		// create square matrix with added RH column
		double[][] m = new double[p][p + 1];
		// create array of precalculated matrix data
		double[] mpc = new double[rs];
		mpc[0] = n;
		for (Map.Entry<Date,Double> pr : data)
		{
			double t = normalize(pr.getKey().getTime() / 1000,min,max);
			// process precalculation array
			for (r = 1; r < rs; r++)
			{
				mpc[r] += Math.pow(t, r);
			}
			// process RH column cells
			m[0][p] += pr.getValue();
			for (r = 1; r < p; r++)
			{
				m[r][p] += Math.pow(t, r) * pr.getValue();
			}
		}
		// populate square matrix section
		for (r = 0; r < p; r++)
		{
			for (c = 0; c < p; c++)
			{
				m[r][c] = mpc[r + c];
			}
		}
		// reduce matrix
		gj_echelonize(m);
		// extract result column
		ArrayList<Double> terms = new ArrayList<>();
		int j = 0;
		for (double[] mc : m)
		{
			terms.add(mc[p]);
		}
		return terms;
	}

	public static class GraphEntry implements Map.Entry<Date,Double>{

		private final Date key;
		private Double value;

		public GraphEntry(Date key, Double value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public Date getKey() {
			return key;
		}

		@Override
		public Double getValue() {
			return value;
		}

		@Override
		public Double setValue(Double object) {
			Double old = value;
			value = object;
			return old;
		}
	}
}
