package my.app.cookbox.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import my.app.cookbox.R;
import my.app.cookbox.layout.LinkedListLayout;

/**
 * Created by Alexander on 015, 15 Jun.
 */

public class ModifyFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.modify_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button ing_b = (Button) getActivity().findViewById(R.id.modify_ingredient_button);
        ing_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandIngredients();

                ImageButton b = (ImageButton) rl.findViewById(R.id.modify_del_button1);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((LinearLayout) v.getParent().getParent()).removeView((RelativeLayout) v.getParent());
                    }
                });
            }
        });

        Button ins_b = (Button) getActivity().findViewById(R.id.modify_instruction_button);
        ins_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 RelativeLayout rl
                        = ((RelativeLayout) getActivity()
                                .getLayoutInflater()
                                .inflate(R.layout.modify_simple_text_item_layout, null));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rl.setLayoutParams(lp);
                LinearLayout ll
                        = (LinearLayout) getActivity().findViewById(R.id.modify_instruction_list);
                ll.addView(rl);

                ImageButton b = (ImageButton) rl.findViewById(R.id.modify_simple_del_button1);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) v.getParent().getParent()).removeView((RelativeLayout) v.getParent());
                    }
                });
            }
        });
        Button ins_b = (Button) getActivity().findViewById(R.id.modify_instruction_button);
        ins_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 RelativeLayout rl
                        = ((RelativeLayout) getActivity()
                                .getLayoutInflater()
                                .inflate(R.layout.modify_simple_text_item_layout, null));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rl.setLayoutParams(lp);
                LinearLayout ll
                        = (LinearLayout) getActivity().findViewById(R.id.modify_instruction_list);
                ll.addView(rl);

                ImageButton b = (ImageButton) rl.findViewById(R.id.modify_simple_del_button1);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) v.getParent().getParent()).removeView((RelativeLayout) v.getParent());
                    }
                });
            }
        });
    }

    private void expandIngredients() {
        RelativeLayout rl
                = ((RelativeLayout) getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.modify_ingredient_item_layout, null));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout ll
                = (LinearLayout) getActivity().findViewById(R.id.modify_ingredient_list);
        ll.addView(rl,lp);
    }

    private void expandInstructions() {

    }

    private void expandComments() {

    }

    private void expandTags() {
        //TAGS are not implemented as of yet.
    }
}
