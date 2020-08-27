package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;

public class GetContactsUseCase {

    private static GetContactsHttpEndpoint mGetContactsHttpEndpoint;

    private List<Listener> mListener =  new ArrayList<>();

    GetContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint){
        mGetContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    void getContactsSync(String filter_term) {

        mGetContactsHttpEndpoint.getContacts(filter_term, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                for(Listener listener : mListener){
                    listener.onGetContacts(getContactsFromContactsSchema(contactSchemas));
                }
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                switch (failReason){
                    case NETWORK_ERROR:
                        notifyNetworkError();
                        break;
                    case GENERAL_ERROR:
                        notifyGeneralError();
                        break;
                    default:
                        throw new RuntimeException();
                }
            }
        });

    }

    private void notifyGeneralError() {
        for(Listener listener : mListener){
            listener.onGetContactsFailed();
        }
    }

    private void notifyNetworkError() {
        for(Listener listener : mListener){
            listener.onGetContactsNetworkError();
        }
    }

    private List<Contact> getContactsFromContactsSchema(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema contactSchema : contactSchemas){
            contacts.add(new Contact(contactSchema.getId(), contactSchema.getFullName(), contactSchema.getImageUrl()));
        }
        return contacts;
    }

    void registerListener(Listener listener) {
        mListener.add(listener);
    }

    void unregisterListener(Listener listener) {
        mListener.remove(listener);
    }

    public interface Listener {
        void onGetContacts(List<Contact> capture);

        void onGetContactsFailed();

        void onGetContactsNetworkError();
    }
}
