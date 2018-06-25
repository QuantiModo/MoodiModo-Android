#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "QuantiModo/QuantiModo-SDK-Android" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

  echo -e "Publishing javadoc...\n"

  mkdir $HOME/javadoc-latest
  cp -R quantimodo-sdk-tools/build/docs/javadoc $HOME/javadoc-latest/qm-tools
  cp -R sdk/build/docs/javadoc $HOME/javadoc-latest/sdk

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/QuantiModo/QuantiModo-SDK-Android gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"

fi