package com.drawin.panchylime.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drawin.panchylime.R;
import com.drawin.panchylime.dialogs.LogOutDialog;
import com.drawin.panchylime.models.User;

/**
 * @author Draco0503
 */
public class UserFragment extends Fragment {

    // THE KEY OF THE ARGUMENTS NEEDED
    private static final String ARG_USER = "user";

    private User _user;

    private View _view;
    private TextView _username;
    private TextView _email;
    private TextView _count;
    private TextView _date;
    private ImageView _ivLogOut;

    /**
     * Empty constructor
     */
    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the UserFragment class
     * @param user user
     * @return UserFragment
     */
    public static UserFragment newInstance(User user) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Is called when the class is created
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _user = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    /**
     * Is called when the view is created
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_user, container, false);

        init();

        return _view;
    }

    /**
     * Initialize all the variables needed
     */
    private void init() {
        _username = _view.findViewById(R.id.fragAcc_username);
        _username.setText(_user.get_username());

        _email = _view.findViewById(R.id.fragAcc_email);
        _email.setText(_user.get_email());

        _count = _view.findViewById(R.id.fragAcc_count);
        _count.setText(String.valueOf(_user.get_bag().size()));

        _date = _view.findViewById(R.id.fragAcc_date);
        _date.setText(_user.get_createdAt());

        _ivLogOut = _view.findViewById(R.id.ivLogOut);
        _ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    /**
     * Displays the LogOutDialog
     */
    private void logOut() {
        LogOutDialog logOutDialog = new LogOutDialog(getActivity());
        logOutDialog.show();

    }

}