package gpdp.nita.com.gpdp4.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.SparseArray;
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
import gpdp.nita.com.gpdp4.models.BlankModel;
import gpdp.nita.com.gpdp4.models.DateModel;
import gpdp.nita.com.gpdp4.models.EditTextModel;
import gpdp.nita.com.gpdp4.models.FormsModel;
import gpdp.nita.com.gpdp4.models.ProfilePicModel;
import gpdp.nita.com.gpdp4.models.RadioGroupModel;
import gpdp.nita.com.gpdp4.models.SpinnerModel;
import gpdp.nita.com.gpdp4.viewholders.BlankViewHolder;
import gpdp.nita.com.gpdp4.viewholders.DateViewHolder;
import gpdp.nita.com.gpdp4.viewholders.EditTextViewHolder;
import gpdp.nita.com.gpdp4.viewholders.ProfilePicViewHolder;
import gpdp.nita.com.gpdp4.viewholders.RadioGroupViewHolder;
import gpdp.nita.com.gpdp4.viewholders.SpinnerViewHolder;


public class FormsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<FormsModel> models;
    private OnValuesEnteredListener onValuesEnteredListener;
    private SparseArray<FormsModel> cache;
    private RecyclerView recyclerView;


    public FormsAdapter(Context context, List<FormsModel> models, OnValuesEnteredListener onValuesEnteredListener) {
        this.context = context;
        this.models = models;
        this.onValuesEnteredListener = onValuesEnteredListener;
        cache = new SparseArray<>();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }


    public void clearCache() {
        cache.clear();
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
        } else if (category == 5) {
            viewHolder = new BlankViewHolder(inflater.inflate(R.layout.layout_blank, viewGroup, false));
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

        String dateStr = dateModel.getDate();


        title.setText(dateModel.getTile());
        date.setText(dateModel.getDate());

        if (!dateStr.equals(""))
            onValuesEnteredListener.onDateSet(dateStr, viewHolder.getAdapterPosition());

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

        int pos = spinnerModel.getSelection();

        if (pos >= 0) {
            spinner.setSelection(spinnerModel.getSelection());
            onValuesEnteredListener.onSpinnerItemSelected
                    (
                            pos,
                            viewHolder.getAdapterPosition()
                    );
        }

        final String[] tokens = spinnerModel.getTokens();
        if (tokens != null)
            onDependentSpinnerItemChosen(viewHolder.getAdapterPosition(), spinnerModel.getSelection(), tokens);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (tokens != null)
                    onDependentSpinnerItemChosen(viewHolder.getAdapterPosition(), position, tokens);

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

        final String[] tokens = radioGroupModel.getTokens();

        if (tokens != null)
            onRadioButtonSelected(viewHolder.getAdapterPosition(), radioGroupModel.getId(), tokens);

        title.setText(radioGroupModel.getTile());
        int id = radioGroupModel.getId();

        Log.d("rbxxx", id + "");

        if (id == -1)
            radioGroup.clearCheck();

        else {
            radioGroup.check(radioGroupModel.getId());
            onValuesEnteredListener.onRadioButtonChecked(radioGroupModel.getId(), viewHolder.getAdapterPosition());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (tokens != null)
                    onRadioButtonSelected(viewHolder.getAdapterPosition(), checkedId, tokens);
                int id = -1;
                if (checkedId == R.id.rb0_rbvh) id = R.id.rb0_rbvh;
                else if (checkedId == R.id.rb1_rbvh) id = R.id.rb1_rbvh;

                onValuesEnteredListener.onRadioButtonChecked(id, viewHolder.getAdapterPosition());
            }
        });
    }

    public void onRadioButtonSelected(final int adapterPos, int checkedId, String[] tokens) {

        String[] hide = null;
        String[] show = null;

        if (tokens != null) {

            if (checkedId == getRadioButtonId(tokens[0])) {
                hide = tokens[1].split(" ");
                show = tokens[2].split(" ");
            } else if (checkedId == getRadioButtonId(tokens[3])) {
                hide = tokens[4].split(" ");
                show = tokens[5].split(" ");
            }
        }


        final String[] finalShow = show;
        final String[] finalHide = hide;

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (finalShow != null && !finalShow[0].equals("-1"))
                    showViews(finalShow, adapterPos);
                if (finalHide != null && !finalHide[0].equals("-1"))
                    hideViews(finalHide, adapterPos);
            }
        });

    }

    private void hideViews(String[] hide, int position) {
        for (String s : hide) {
            int pos = Integer.parseInt(s);

            if (cache.get((pos + position), null) == null && (pos + position) < models.size()) {
                cache.put((pos + position), models.get(pos + position));
                models.set(pos + position, new BlankModel("", -1));

                onValuesEnteredListener.onViewRemoved(pos + position, getCategory(models.get(pos + position)));

                notifyItemChanged(pos + position);
            }
        }
    }

    private int getCategory(FormsModel formsModel) {
        if (formsModel instanceof RadioGroupModel) return 0;
        else if (formsModel instanceof EditTextModel) return 1;
        else if (formsModel instanceof SpinnerModel) return 2;
        else if (formsModel instanceof DateModel) return 3;
        else if (formsModel instanceof ProfilePicModel) return 4;
        else if (formsModel instanceof BlankModel) return 5;
        else return -1;
    }


    private void showViews(String[] show, int position) {
        for (String s : show) {
            int pos = Integer.parseInt(s);
            if (cache.get((pos + position)) != null) {
                models.set(pos + position, cache.get((pos + position)));
                cache.remove((pos + position));
                notifyItemChanged(pos + position);
            }
        }
    }

    private int getRadioButtonId(String id) {
        switch (id) {
            case "0":
                return R.id.rb0_rbvh;
            case "1":
                return R.id.rb1_rbvh;
            default:
                return -1;
        }
    }

    public void onDependentSpinnerItemChosen(final int anchor, int chosenPosition, String[] tokens) {

        String[] hide = null;
        String[] show = null;

        Log.d("posixxx", chosenPosition + "");

        if (tokens != null) {

            if (chosenPosition == Integer.parseInt(tokens[0])) {
                hide = tokens[1].split(" ");
                show = tokens[2].split(" ");
            } else {
                hide = tokens[2].split(" ");
                show = tokens[1].split(" ");
            }
        }

        final String[] finalShow = show;
        final String[] finalHide = hide;

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (finalShow != null && !finalShow[0].equals("-1"))
                    showViews(finalShow, anchor);
                if (finalHide != null && !finalHide[0].equals("-1"))
                    hideViews(finalHide, anchor);
            }
        });

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
        else if (models.get(position) instanceof BlankModel) return 5;
        else return -1;
    }

}
