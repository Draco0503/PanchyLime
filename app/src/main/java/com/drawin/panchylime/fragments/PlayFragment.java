package com.drawin.panchylime.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.drawin.panchylime.R;
import com.drawin.panchylime.activities.IndexActivity;
import com.drawin.panchylime.dialogs.InfoDialog;
import com.drawin.panchylime.games.savetheslime.SaveSlimeActivity;
import com.drawin.panchylime.games.tictactoe.TicTacToeActivity;
import com.drawin.panchylime.models.User;

import java.io.Serializable;

/**
 * @author Draco0503
 */
public class PlayFragment extends Fragment {

    // THE KEY OF THE ARGUMENTS NEEDED
    private static final String ARG_USER = "user";
    private static final String ARG_IMGURL = "img";

    private String _imageURL;
    private View _view;
    private User _user;

    private ImageView _ivInfo1;
    private ImageView _ivPlay1;
    private ImageView _ivInfo2;
    private ImageView _ivPlay2;

    /**
     * Empty constructor
     */
    public PlayFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the PlayFragment class
     * @param imageURL selected pet image
     * @param user user
     * @return PlayFragment
     */
    public static PlayFragment newInstance(User user, String imageURL) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putString(ARG_IMGURL, imageURL);
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
            _imageURL = getArguments().getString(ARG_IMGURL);
        }
    }

    /**
     * Is called when the view is created
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_play, container, false);

        init();

        return _view;
    }

    /**
     * Initialize all the variables needed
     */
    private void init() {
        _ivInfo1 = _view.findViewById(R.id.ivInfo1);
        _ivInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo(0);
            }
        });

        _ivPlay1 = _view.findViewById(R.id.ivPlay1);
        _ivPlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame(0);
            }
        });
        _ivInfo2 = _view.findViewById(R.id.ivInfo2);
        _ivInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo(1);
            }
        });

        _ivPlay2 = _view.findViewById(R.id.ivPlay2);
        _ivPlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame(1);
            }
        });
    }

    /**
     * Shows a dialog with the information of each game
     * @param game
     */
    private void showInfo(int game) {
        InfoDialog infoDialog;
        switch (game) {
            case 0:
                infoDialog = new InfoDialog(getActivity(), R.string.game_1_desc, R.string.game_1_title);
                infoDialog.show();
                break;
            case 1:
                infoDialog = new InfoDialog(getActivity(), R.string.game_2_desc, R.string.game_2_title);
                infoDialog.show();
                break;
        }
    }

    /**
     * Game selector
     * @param game
     */
    private void playGame(int game) {

        IndexActivity.BGM_GAMES_PANCHYLIME.start();

        Intent game_view;
        switch (game) {
            case 0:
                game_view = new Intent(getActivity(), SaveSlimeActivity.class);
                game_view.putExtra("username", _user.get_username());
                game_view.putExtra("pet", _imageURL);
                startActivity(game_view);
                break;
            case 1:
                game_view = new Intent(getActivity(), TicTacToeActivity.class);
                game_view.putExtra("username", _user.get_username());
                startActivity(game_view);
                break;
        }
    }

}