package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.jetbrains.annotations.NotNull;

import static com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;

public class FetchUserUseCaseSyncImp implements FetchUserUseCaseSync {

    private final FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private final UsersCache usersCache;

    public FetchUserUseCaseSyncImp(FetchUserHttpEndpointSync mFetchUserHttpEndpointSync, UsersCache mUsersCache){
        fetchUserHttpEndpointSync = mFetchUserHttpEndpointSync;
        usersCache = mUsersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        EndpointResult endPointResult;
        User u;
        if (null==usersCache.getUser(userId)){
            try {
                endPointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, null);
            }

            u = new User(endPointResult.getUserId(),endPointResult.getUsername());

            return getUseCaseResult(endPointResult, u);
        }
        else {
            return new UseCaseResult(Status.SUCCESS, usersCache.getUser(userId));
        }
    }

    @NotNull
    private UseCaseResult getUseCaseResult(EndpointResult endPointResult, User u) {
        switch (endPointResult.getStatus()){
            case SUCCESS:{
                usersCache.cacheUser(u);
                return new UseCaseResult(Status.SUCCESS, u);
            }
            default:
                return new UseCaseResult(Status.FAILURE, null);
        }
    }

}
