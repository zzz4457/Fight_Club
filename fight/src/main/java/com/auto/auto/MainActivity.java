package com.auto.auto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.auto.auto.Fragment.FirstFragment;
import com.auto.auto.Fragment.OneButtonFragment;
import com.auto.auto.Fragment.ZeroFragment;
import com.auto.auto.Model.Account;
import com.auto.auto.stepperview.OnCancelAction;
import com.auto.auto.stepperview.OnContinueAction;
import com.auto.auto.stepperview.OnFinishAction;
import com.auto.auto.stepperview.SteppersItem;
import com.auto.auto.stepperview.SteppersView;
import com.newland.support.nllogger.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FirstFragment.OnFirstFragmentListener {

    private List<SteppersItem> steppersItems = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SteppersView.Config config = createStepViewConfig();
        steppersItems = createSteps();

        SteppersView steppersView = (SteppersView) findViewById(R.id.stepperView);
        steppersView.setConfig(config);
        steppersView.setItems(steppersItems);
        steppersView.build();
    }

    private SteppersView.Config createStepViewConfig() {
        final SteppersView.Config config = new SteppersView.Config();
        config.setOnFinishAction(new OnFinishAction() {
            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });

        config.setOnContinueAction(new OnContinueAction() {
            @Override
            public void onContinue(int position) {
                switch (position) {
                    case 1:
                        SteppersItem item = steppersItems.get(position);
                        FirstFragment fragment = (FirstFragment) item.getFragment();

                        String dingTalkAccount = fragment.getDingTalkAccount();
                        String dingTalkPassword = fragment.getDingTalkPassword();
                        String authAccount = fragment.getAuthAccount();
                        String authPassword = fragment.getAuthAccountPassword();
                        String email = fragment.geteMail();

                        saveLoginInfo(dingTalkAccount, dingTalkPassword, authAccount, authPassword, email);

                        break;
                    default:
                        break;
                }
            }
        });

        config.setOnCancelAction(new OnCancelAction() {
            @Override
            public void onCancel(int position) {

                switch (position) {
                    case 0:
                        backToLoginActivity(MainActivity.this);
                        finish();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Not finished yet", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        config.setFragmentManager(getSupportFragmentManager());

        return config;
    }

    private List<SteppersItem> createSteps() {

        ArrayList<SteppersItem> steps = new ArrayList<>();

        SteppersItem zeroStep = new SteppersItem();
        zeroStep.setLabel(getString(R.string.step_zero));
        zeroStep.setSubLabel(getString(R.string.step_zero_instruction));
        zeroStep.setFragment(new ZeroFragment());
        zeroStep.setPositiveButtonEnable(true);

        steps.add(zeroStep);

        SteppersItem firstStep = new SteppersItem();
        firstStep.setLabel(getString(R.string.step_one));
        firstStep.setSubLabel(getString(R.string.step_one_instruction));
        firstStep.setFragment(new FirstFragment());
        firstStep.setPositiveButtonEnable(false);

        steps.add(firstStep);

        SteppersItem secondStep = new SteppersItem();
        secondStep.setLabel(getString(R.string.step_two));
        secondStep.setSubLabel(getString(R.string.step_two_instruction));

        OneButtonFragment thirdFragment = OneButtonFragment.newInstance(getString(R.string.open_accessibility));
        thirdFragment.setOneButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation.openAccessibilitySetting(MainActivity.this);
            }
        });

        secondStep.setFragment(thirdFragment);
        secondStep.setPositiveButtonEnable(true);

        steps.add(secondStep);

        SteppersItem thirdStep = new SteppersItem();
        thirdStep.setLabel(getString(R.string.step_three));
        thirdStep.setLabel(getString(R.string.step_three_instruction));

        OneButtonFragment forthFragment = OneButtonFragment.newInstance(getString(R.string.set_time));
        forthFragment.setOneButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.openSettings(MainActivity.this);
            }
        });
        thirdStep.setFragment(forthFragment);
        thirdStep.setPositiveButtonEnable(true);

        steps.add(thirdStep);

        return steps;
    }

    private void saveLoginInfo(String dingTalkAccount, String dingTalkPassword, String authAccount, String authPassword, String email) {

        Account account = new Account();
        account.setPhoneNum(dingTalkAccount);
        account.setDingDingPassword(dingTalkPassword);
        account.setAuthAccount(authAccount);
        account.setAuthAccountPassword(authPassword);
        account.setMail(email);

        account.saveAccountInfo(account, this);
        LogUtils.d("$$$ 账户信息已保存");
    }


    private void backToLoginActivity(Context context) {

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    private static boolean openSettings(Context context) {

        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        boolean isOk = true;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            isOk = false;
        }
        return isOk;
    }

    @Override
    public void onContentChanged(boolean isInfoEnough) {

        SteppersItem item = steppersItems.get(1);

        if (isInfoEnough) {
            item.setPositiveButtonEnable(true);

        } else {
            if (item.isPositiveButtonEnable()) {
                item.setPositiveButtonEnable(false);
            }
        }
    }
}
