package id.co.ppu.collectionfast2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormModel;

import id.co.ppu.collectionfast2.R;

/**
 * Created by Eric on 30-Aug-16.
 */
public abstract class FormFragment extends Fragment {

    private FormController formController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_activity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        formController = new FormController(getActivity());
        initForm();

        ViewGroup containerView = (ViewGroup) getActivity().findViewById(R.id.form_elements_container);
//        formController.addFormElementsToView(containerView);
        formController.recreateViews(containerView);
    }

    /**
     * An abstract method that must be overridden by subclasses where the form fields are initialized.
     */
    protected abstract void initForm();

    /**
     * Returns the associated form controller
     */
    public FormController getFormController() {
        return formController;
    }

    /**
     * Returns the associated form model
     */
    public FormModel getModel() {
        return formController.getModel();
    }

    public void setTitle(String s) {
        getActivity().setTitle(s);
    }

}

