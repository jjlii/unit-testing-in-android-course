package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.GetReputationUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/*
The three rules:
1) You are not allowed to write any production code unless it is to make a failing unit test pass
2) You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures
3) You are not allowed to write any more production code than is sufficient to pass the one failing unit test

The requirements:
1) If the server request completes successfully, then use case should indicate successful completion of the flow.
2) If the server request completes successfully, then the fetched reputation should be returned
3) If the server request fails for any reason, the use case should indicate that the flow failed.
4) If the server request fails for any reason, the returned reputation should be 0.

You should:
1) Create a new class FetchReputationUseCaseSync WITHOUT writing any actual functionality.
2) Create a new test class.
3) Test drive the implementation of FetchReputationUseCaseSync according to Uncle Bob's three rules of TDD.
   Always run all the tests to make sure that further changes don't break the existing functionality.
4) Refactor the production code.
5) Run the tests to make sure that all of them pass after the refactoring.
6) Refactor the tests code.
7) Run the tests to make sure that all of them pass after the refactoring.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetReputationUseCaseSyncTest {

    private static GetReputationUseCaseSync SUT;

    @Mock
    GetReputationHttpEndpointSync mGetReputationHttpEndpointSyncMock;

    @Before
    public void setUp() throws Exception {
        SUT = new GetReputationUseCaseSync(mGetReputationHttpEndpointSyncMock);
    }

    @Test
    public void getReputation_success_successReturned() {
        success();
        UseCaseResult result = SUT.getReputationSync();

        assertThat(result.getStatus(), is(GetReputationUseCaseSync.Status.SUCCESS));
        assertThat(result.getReputation(), is(1));
    }

    @Test
    public void getReputation_generalError_failureReturned() {
        generalError();

        UseCaseResult result = SUT.getReputationSync();

        assertThat(result.getStatus(), is(GetReputationUseCaseSync.Status.FAILURE));
        assertThat(result.getReputation(), is(0));
    }

    @Test
    public void getReputation_networkError_failureReturned() {
        networkError();

        UseCaseResult result = SUT.getReputationSync();

        assertThat(result.getStatus(), is(GetReputationUseCaseSync.Status.FAILURE));
        assertThat(result.getReputation(), is(0));
    }

    private void success(){
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS,1));
    }

    private void generalError() {
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, 0));
    }

    private void networkError(){
        when(mGetReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, 0));
    }
}
