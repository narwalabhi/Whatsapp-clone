package com.narwal.whatsappclone;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.narwal.whatsappclone.model.User;


public class PeopleFragment extends Fragment {

    private static final int NORMAL_VIEW_TYPE = 2;
    private static final int DELETED_VIEW_TYPE = 1;

    FirestorePagingAdapter<User, RecyclerView.ViewHolder> pagingAdapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Query db;
    RecyclerView rvPeople;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = firebaseFirestore.collection(getString(R.string.userRefKey)).orderBy("name", Query.Direction.ASCENDING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        setUpAdapter();
        rvPeople = v.findViewById(R.id.recyclerView);
        return v;
    }

    private void setUpAdapter() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setPrefetchDistance(2)
                .setEnablePlaceholders(false)
                .build();
        FirestorePagingOptions options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(db, config, User.class)
                .build();
        pagingAdapter = new FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i, @NonNull User user) {
                if (holder instanceof UserViewHolder) {
                    ((UserViewHolder) holder).bind(user, requireContext());
                } else {
                    Log.d("People", "onBindViewHolder: empty");
                }
            }

            @Override
            public int getItemViewType(int position) {
                super.getItemViewType(position);
                User item = getItem(position).toObject(User.class);
                if (item.getUid().equals(firebaseAuth.getUid())) {
                    return DELETED_VIEW_TYPE;
                } else {
                    return NORMAL_VIEW_TYPE;
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
                if (viewType == NORMAL_VIEW_TYPE) {
                    View view = layoutInflater.inflate(R.layout.list_item, parent, false);
                    return new UserViewHolder(view);
                }
                return new EmptyViewHolder(layoutInflater.inflate(R.layout.empty_item, parent, false));
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state) {
//                    case LOADING_INITIAL:
//                        break;
//                    case LOADING_MORE:
//                        mSwipeRefreshLayout.setRefreshing(true);
//                        break;

//                    case LOADED:
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        break;

                    case ERROR:
                        Toast.makeText(
                                requireContext(),
                                "Error Occurred!",
                                Toast.LENGTH_SHORT
                        ).show();

//                        mSwipeRefreshLayout.setRefreshing(false);
//                        break;

//                    case FINISHED:
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        break;
                }
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPeople.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPeople.setAdapter(pagingAdapter);
    }
}