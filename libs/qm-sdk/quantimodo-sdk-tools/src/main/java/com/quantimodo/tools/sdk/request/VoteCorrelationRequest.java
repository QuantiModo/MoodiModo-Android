package com.quantimodo.tools.sdk.request;

import com.quantimodo.android.sdk.SdkResponse;
import com.quantimodo.android.sdk.model.CorrelationPost;

/**
 * Submit vote on correlation
 */
public class VoteCorrelationRequest extends SdkRequest<Void> {

    private final CorrelationPost mCorrelation;

    public VoteCorrelationRequest(CorrelationPost mCorrelation) {
        super(Void.class);
        this.mCorrelation = mCorrelation;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        SdkResponse<Void> response = getClient().voteCorrelation(getCtx(), getToken(), mCorrelation);
        checkResponse(response);
        return null;
    }
}
