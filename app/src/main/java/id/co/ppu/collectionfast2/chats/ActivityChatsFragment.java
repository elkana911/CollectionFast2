package id.co.ppu.collectionfast2.chats;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.DividerItemDecoration;
import id.co.ppu.collectionfast2.component.RealmSearchView;
import id.co.ppu.collectionfast2.listener.OnContactsListener;
import io.realm.Realm;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActivityChatsFragment extends Fragment {

    private Realm realm;

    @BindView(R.id.contacts)
    RealmSearchView contacts;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private OnContactsListener mListener;

    public ActivityChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_chats, container, false);

        ButterKnife.bind(this, view);
        contacts.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        this.realm = Realm.getDefaultInstance();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnContactsListener) {
            mListener = (OnContactsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void search(String query) {
        contacts.getSearchBar().setText(query);
    }


    @OnClick(R.id.fab)
    public void onClickFab() {

    }

    /*
    public class ContactListAdapter extends RealmSearchAdapter<DisplayTrnLDVDetails, ContactListAdapter.DataViewHolder> {

        public ContactListAdapter(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey) {
            super(context, realm, filterKey);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_contact_list, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {
            final DisplayTrnLDVDetails detail = realmResults.get(position);
            dataViewHolder.llRowContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnContactsListener) {
                        ((OnContactsListener)getContext()).onContactSelected();
                    }

                }
            });

            dataViewHolder.llRowContact.setBackgroundColor(Color.WHITE);    // must

            TextView contactName = dataViewHolder.tvContactName;
            if (Build.VERSION.SDK_INT >= 24) {
                contactName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                contactName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>"));
            }
        }

        public class DataViewHolder extends RealmSearchViewHolder {

            public FrameLayout container;

            @BindView(R.id.llRowContact)
            LinearLayout llRowContact;

            @BindView(R.id.tvContactName)
            TextView tvContactName;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);
            }

        }
    }
    */

}
