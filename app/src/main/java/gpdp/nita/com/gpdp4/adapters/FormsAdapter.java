package gpdp.nita.com.gpdp4.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.interfaces.OnDateSet;
import gpdp.nita.com.gpdp4.interfaces.OnValuesEnteredListener;
import gpdp.nita.com.gpdp4.models.DateModel;
import gpdp.nita.com.gpdp4.models.EditTextModel;
import gpdp.nita.com.gpdp4.models.FormsModel;
import gpdp.nita.com.gpdp4.models.ProfilePicModel;
import gpdp.nita.com.gpdp4.models.RadioGroupModel;
import gpdp.nita.com.gpdp4.models.SpinnerModel;
import gpdp.nita.com.gpdp4.viewholders.DateViewHolder;
import gpdp.nita.com.gpdp4.viewholders.EditTextViewHolder;
import gpdp.nita.com.gpdp4.viewholders.ProfilePicViewHolder;
import gpdp.nita.com.gpdp4.viewholders.RadioGroupViewHolder;
import gpdp.nita.com.gpdp4.viewholders.SpinnerViewHolder;


public class FormsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<FormsModel> models;
    private OnValuesEnteredListener onValuesEnteredListener;

    public FormsAdapter(Context context, List<FormsModel> models, OnValuesEnteredListener onValuesEnteredListener) {
        this.context = context;
        this.models = models;
        this.onValuesEnteredListener = onValuesEnteredListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int category) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder = null;

        if (category == 0) {
            viewHolder = new RadioGroupViewHolder(inflater.inflate(R.layout.layout_rb, viewGroup, false));
        } else if (category == 1) {
            viewHolder = new EditTextViewHolder(inflater.inflate(R.layout.layout_edit_text, viewGroup, false));
        } else if (category == 2) {
            viewHolder = new SpinnerViewHolder(inflater.inflate(R.layout.layout_spinner, viewGroup, false));
        } else if (category == 3) {
            viewHolder = new DateViewHolder(inflater.inflate(R.layout.layout_date, viewGroup, false));
        } else if (category == 4) {
            viewHolder = new ProfilePicViewHolder(inflater.inflate(R.layout.layout_profile_pic, viewGroup, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int category = viewHolder.getItemViewType();
        if (category == 0) bindRadioGroup((RadioGroupViewHolder) viewHolder, position);
        else if (category == 1) bindEditText((EditTextViewHolder) viewHolder, position);
        else if (category == 2) bindSpinner((SpinnerViewHolder) viewHolder, position);
        else if (category == 3) bindDate((DateViewHolder) viewHolder, position);
        else if (category == 4) bindProfilePic((ProfilePicViewHolder) viewHolder, position);
    }

    private void bindProfilePic(final ProfilePicViewHolder viewHolder, int position) {
        ProfilePicModel profilePicModel = (ProfilePicModel) models.get(position);

        final CircleImageView dp = viewHolder.getProfilePic();
        TextView benCode = viewHolder.getBenCode();

        benCode.setText(profilePicModel.getBenCode());

        Glide
                .with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .load(profilePicModel.getImgUrl())
                .into(dp);


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onValuesEnteredListener.onProfilePicTapped(dp, viewHolder.getAdapterPosition());
            }
        });

    }

    private void bindDate(final DateViewHolder viewHolder, int position) {

        DateModel dateModel = (DateModel) models.get(position);

        TextView title = viewHolder.getTextView();
        TextView date = viewHolder.getDateField();
        ConstraintLayout root = viewHolder.getRoot();

        title.setText(dateModel.getTile());
        date.setText(dateModel.getDate());

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.linkPicker(context, new OnDateSet() {
                    @Override
                    public void onDateSet(String date) {
                        onValuesEnteredListener.onDateSet(date, viewHolder.getAdapterPosition());
                    }
                });
            }
        });


    }

    private void bindSpinner(final SpinnerViewHolder viewHolder, int position) {

        final SpinnerModel spinnerModel = (SpinnerModel) models.get(position);

        TextView title = viewHolder.getTextView();
        final Spinner spinner = viewHolder.getSpinner();

        title.setText(spinnerModel.getTile());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                spinnerModel.getMenu());

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setSelection(spinnerModel.getSelection());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerModel.setSelection(position);
                onValuesEnteredListener.onSpinnerItemSelected
                        (
                                spinnerModel.getKeys().get(position),
                                viewHolder.getAdapterPosition()
                        );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void bindEditText(final EditTextViewHolder viewHolder, int position) {

        EditTextModel editTextModel = (EditTextModel) models.get(position);

        TextView title = viewHolder.getQuestion();
        EditText editText = viewHolder.getEditText();
        int inputType = editTextModel.getInputType();

        if (inputType == 0)
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        else if (inputType == 1)
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        else if (inputType == 2) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onValuesEnteredListener.onTyping(s.toString(), viewHolder.getAdapterPosition());
            }
        });


        title.setText(editTextModel.getTile());
        editText.setText(editTextModel.getTextInEditText());

        if (!editTextModel.isEnabled())
            editText.setEnabled(false);
        else
            editText.setEnabled(true);


    }

    private void bindRadioGroup(final RadioGroupViewHolder viewHolder, int position) {

        final RadioGroupModel radioGroupModel = (RadioGroupModel) models.get(position);

        TextView title = viewHolder.getTextView();
        RadioGroup radioGroup = viewHolder.getRadioGroup();

        title.setText(radioGroupModel.getTile());
        radioGroup.check(radioGroupModel.getId());


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onRadioButtonSelected(viewHolder.getAdapterPosition(), checkedId, radioGroupModel);
                int id = -1;
                if (checkedId == R.id.rb0_rbvh) id = 1;
                else if (checkedId == R.id.rb1_rbvh) id = 0;
                onValuesEnteredListener.onRadioButtonChecked(id, viewHolder.getAdapterPosition());
            }
        });
    }

    private void onRadioButtonSelected(int adapterPosition, int checkedId, RadioGroupModel radioGroupModel) {
        if (checkedId == radioGroupModel.getSkipButtonId()) {
//            onValuesEnteredListener.onViewRemoved(adapterPosition+1,radioGroupModel.getSkips());
        }
    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (models.get(position) instanceof RadioGroupModel) return 0;
        else if (models.get(position) instanceof EditTextModel) return 1;
        else if (models.get(position) instanceof SpinnerModel) return 2;
        else if (models.get(position) instanceof DateModel) return 3;
        else if (models.get(position) instanceof ProfilePicModel) return 4;
        else return -1;
    }

}
