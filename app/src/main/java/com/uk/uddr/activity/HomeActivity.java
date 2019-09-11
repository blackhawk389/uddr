package com.uk.uddr.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uk.uddr.R;
import com.uk.uddr.fragment.CartFragment;
import com.uk.uddr.fragment.FavouriteStoresFragment;
import com.uk.uddr.fragment.HomeFragment;
import com.uk.uddr.fragment.MyOrderFragment;
import com.uk.uddr.fragment.PartnerprofileFragment;
import com.uk.uddr.fragment.ProfileFragment;
import com.uk.uddr.fragment.RatingListFragment;
import com.uk.uddr.fragment.SearchCategory;
import com.uk.uddr.utils.DBHelper;
import com.uk.uddr.utils.Utils;

public class HomeActivity extends AppCompatActivity {

    LinearLayout len_home, len_order, len_profile,len_search;
    //RelativeLayout len_cart;
    private static ImageView img_home, img_order, img_profile, img_search;
    private static TextView txt_home, txt_order, txt_profile, txt_search;
    private static Button btn_viewbasket;
    SharedPreferences sharedPreferences;
    private static DBHelper dbHelper;
    private static Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        mResources = getResources();
        sharedPreferences = getSharedPreferences(Utils.sharedfilename, MODE_PRIVATE);
        init();
        loadFragment(new HomeFragment());
        dbHelper = new DBHelper(this);
        updatecart();
    }

    private void init() {
        len_home = findViewById(R.id.len_home);
        len_order = findViewById(R.id.len_order);
        len_profile = findViewById(R.id.len_profile);
        len_search = findViewById(R.id.len_search);
        btn_viewbasket = findViewById(R.id.btn_viewbasket);

        img_home = findViewById(R.id.img_home);
        img_order = findViewById(R.id.img_order);
        img_profile = findViewById(R.id.img_profile);
        img_search = findViewById(R.id.img_search);

        txt_home = findViewById(R.id.txt_home);
        txt_order = findViewById(R.id.txt_order);
        txt_profile = findViewById(R.id.txt_profile);
        txt_search = findViewById(R.id.txt_search);

        len_home.setOnClickListener(clickListener);
        len_order.setOnClickListener(clickListener);
        len_profile.setOnClickListener(clickListener);
        len_search.setOnClickListener(clickListener);
        btn_viewbasket.setOnClickListener(clickListener);

        btn_viewbasket.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (btn_viewbasket.getRight() - btn_viewbasket.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        btn_viewbasket.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });


        img_home.setImageResource(R.mipmap.ic_homeicon_w);
        txt_home.setTextColor(getResources().getColor(R.color.white));
        txt_order.setTextColor(getResources().getColor(R.color.light_grey));
        txt_search.setTextColor(getResources().getColor(R.color.light_grey));
        txt_profile.setTextColor(getResources().getColor(R.color.light_grey));

        img_profile.setImageResource(R.mipmap.ic_profileicon);
        img_order.setImageResource(R.mipmap.ic_myorder);
        img_search.setImageResource(R.mipmap.tab_usearch);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {

                case R.id.btn_viewbasket:
                    Intent basket = new Intent(HomeActivity.this, Basket.class);
                    basket.putExtra("t_id","-1");
                    startActivity(basket);
                    break;
                case R.id.len_home:
                    img_home.setImageResource(R.mipmap.ic_homeicon_w);
                    txt_home.setTextColor(getResources().getColor(R.color.white));
                    img_order.setImageResource(R.mipmap.ic_myorder);
                    txt_order.setTextColor(getResources().getColor(R.color.light_grey));
                    img_profile.setImageResource(R.mipmap.ic_profileicon);
                    txt_profile.setTextColor(getResources().getColor(R.color.light_grey));
                    img_search.setImageResource(R.mipmap.tab_usearch);
                    txt_search.setTextColor(getResources().getColor(R.color.light_grey));
                    loadFragment(new HomeFragment());
                    break;

                case R.id.len_order:
                    if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
                        Intent login = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(login);
                    } else {
                        img_order.setImageResource(R.mipmap.ic_myorder_w);
                        txt_order.setTextColor(getResources().getColor(R.color.white));
                        img_home.setImageResource(R.mipmap.ic_homeicon);
                        txt_home.setTextColor(getResources().getColor(R.color.light_grey));
                        img_profile.setImageResource(R.mipmap.ic_profileicon);
                        txt_profile.setTextColor(getResources().getColor(R.color.light_grey));
                        img_search.setImageResource(R.mipmap.tab_usearch);
                        txt_search.setTextColor(getResources().getColor(R.color.light_grey));
                        loadFragment(new MyOrderFragment());
                    }
                    Log.e("order","working");
                    break;

                case R.id.len_search:
                    if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
                        Intent login = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(login);
                    } else {
//                        Intent basket = new Intent(HomeActivity.this, Basket.class);
//                        basket.putExtra("t_id","-1");
//                        startActivity(basket);

                        loadFragment(new SearchCategory());
                        img_search.setImageResource(R.mipmap.tab_ssearch);
                        txt_search.setTextColor(getResources().getColor(R.color.white));
                        img_order.setImageResource(R.mipmap.ic_myorder);
                        txt_order.setTextColor(getResources().getColor(R.color.light_grey));
                        img_home.setImageResource(R.mipmap.ic_homeicon);
                        txt_home.setTextColor(getResources().getColor(R.color.light_grey));
                        img_profile.setImageResource(R.mipmap.ic_profileicon);
                        txt_profile.setTextColor(getResources().getColor(R.color.light_grey));




                        /*img_cart.setImageResource(R.mipmap.ic_carticon_w);
                        txt_cart.setTextColor(getResources().getColor(R.color.white));
                        img_order.setImageResource(R.mipmap.ic_myorder);
                        txt_order.setTextColor(getResources().getColor(R.color.light_grey));
                        img_home.setImageResource(R.mipmap.ic_homeicon);
                        txt_home.setTextColor(getResources().getColor(R.color.light_grey));
                        img_profile.setImageResource(R.mipmap.ic_profileicon);
                        txt_profile.setTextColor(getResources().getColor(R.color.light_grey));
                        CartFragment cartFragment = new CartFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("t_id", "-1");
                        cartFragment.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, cartFragment);
                        fragmentTransaction.commit(); // save the changes*/


                    }
                    Log.e("search","working");
                    break;

                case R.id.len_profile:

//                    img_profile.setImageResource(R.mipmap.ic_profile_w);
//                        txt_profile.setTextColor(getResources().getColor(R.color.white));
//                        img_home.setImageResource(R.mipmap.ic_homeicon);
//                        txt_home.setTextColor(getResources().getColor(R.color.light_grey));
//                        img_order.setImageResource(R.mipmap.ic_myorder);
//                        txt_order.setTextColor(getResources().getColor(R.color.light_grey));
//                        img_cart.setImageResource(R.mipmap.ic_cart);
//                        txt_cart.setTextColor(getResources().getColor(R.color.light_grey));
//                        loadFragment(new FavouriteStoresFragment());


                    if (sharedPreferences.getString(Utils.user_id, "").equals("")) {
                        Intent login = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(login);
                    } else {
                        img_profile.setImageResource(R.mipmap.ic_profile_w);
                        txt_profile.setTextColor(getResources().getColor(R.color.white));
                        img_home.setImageResource(R.mipmap.ic_homeicon);
                        txt_home.setTextColor(getResources().getColor(R.color.light_grey));
                        img_order.setImageResource(R.mipmap.ic_myorder);
                        txt_order.setTextColor(getResources().getColor(R.color.light_grey));
                        img_search.setImageResource(R.mipmap.tab_usearch);
                        txt_search.setTextColor(getResources().getColor(R.color.light_grey));
                        loadFragment(new ProfileFragment());
                    }
                    break;


            }
        }
    };

    public static void updatecart() {
        Cursor cursor = dbHelper.selectallproduct();
        if (cursor.getCount() > 0) {
            btn_viewbasket.setVisibility(View.VISIBLE);
        } else {
            btn_viewbasket.setVisibility(View.GONE);
        }

    }

    public static void changeicon(int pos) {
        switch (pos) {

            case 1:
                img_home.setImageResource(R.mipmap.ic_homeicon_w);
                txt_home.setTextColor(mResources.getColor(R.color.white));
                txt_order.setTextColor(mResources.getColor(R.color.light_grey));
                txt_search.setTextColor(mResources.getColor(R.color.light_grey));
                txt_profile.setTextColor(mResources.getColor(R.color.light_grey));
                img_profile.setImageResource(R.mipmap.ic_profileicon);
                img_order.setImageResource(R.mipmap.ic_myorder);
                img_search.setImageResource(R.mipmap.tab_usearch);
                break;

            case 2:
                img_order.setImageResource(R.mipmap.ic_myorder);
                txt_order.setTextColor(mResources.getColor(R.color.light_grey));
                img_home.setImageResource(R.mipmap.ic_homeicon);
                txt_home.setTextColor(mResources.getColor(R.color.light_grey));
                img_profile.setImageResource(R.mipmap.ic_profileicon);
                txt_profile.setTextColor(mResources.getColor(R.color.light_grey));
                img_search.setImageResource(R.mipmap.tab_ssearch);
                txt_search.setTextColor(mResources.getColor(R.color.white));
                break;

            case 3:
                img_search.setImageResource(R.mipmap.tab_usearch);
                txt_search.setTextColor(mResources.getColor(R.color.light_grey));
                img_order.setImageResource(R.mipmap.ic_myorder_w);
                txt_order.setTextColor(mResources.getColor(R.color.white));
                img_home.setImageResource(R.mipmap.ic_homeicon);
                txt_home.setTextColor(mResources.getColor(R.color.light_grey));
                img_profile.setImageResource(R.mipmap.ic_profileicon);
                txt_profile.setTextColor(mResources.getColor(R.color.light_grey));
                break;

            case 4:
                img_profile.setImageResource(R.mipmap.ic_profile_w);
                txt_profile.setTextColor(mResources.getColor(R.color.white));
                img_home.setImageResource(R.mipmap.ic_homeicon);
                txt_home.setTextColor(mResources.getColor(R.color.light_grey));
                img_order.setImageResource(R.mipmap.ic_myorder);
                txt_order.setTextColor(mResources.getColor(R.color.light_grey));
                img_search.setImageResource(R.mipmap.tab_usearch);
                txt_search.setTextColor(mResources.getColor(R.color.light_grey));
                break;
        }
    }

    private void loadFragment(Fragment fragment) {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}

