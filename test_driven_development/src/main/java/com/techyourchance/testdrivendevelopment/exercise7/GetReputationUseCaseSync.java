package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class GetReputationUseCaseSync {

    private static GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public GetReputationUseCaseSync(GetReputationHttpEndpointSync mGetReputationHttpEndpointSync){
        getReputationHttpEndpointSync = mGetReputationHttpEndpointSync;
    }
    public enum Status{
        SUCCESS,
        FAILURE
    }

    public UseCaseResult getReputationSync() {

        GetReputationHttpEndpointSync.EndpointResult result = getReputationHttpEndpointSync.getReputationSync();
        switch (result.getStatus()) {
            case SUCCESS:
                return new UseCaseResult(Status.SUCCESS, result.getReputation());
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return new UseCaseResult(Status.FAILURE, 0);
            default:
                throw new RuntimeException();
        }
    }

    class UseCaseResult{
        private final Status status;

        private final int reputation;

        UseCaseResult(Status status, int reputation){
            this.status = status;
            this.reputation = reputation;
        }

        public Status getStatus() {
            return status;
        }

        public int getReputation() {
            return reputation;
        }
    }

}
