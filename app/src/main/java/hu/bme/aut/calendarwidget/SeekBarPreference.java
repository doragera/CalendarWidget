/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Improvements :
 * - save the value on positive button click, not on seekbar change
 * - handle @string/... values in xml file
 */

package hu.bme.aut.calendarwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, OnClickListener
{
    // ------------------------------------------------------------------------------------------
    // Private attributes :
    private static final String android_ns = "http://schemas.android.com/apk/res/android";

    private SeekBar mSeekBar;
    private TextView mValueText;

    private String mDialogMessage, mSuffix;
    private int mDefault, mMax, mValue = 0;
    // ------------------------------------------------------------------------------------------

    private String getAttributeStringValue(Context context, AttributeSet attrs, String key)
    {
        int mDialogMessageId = attrs.getAttributeResourceValue(android_ns, key, 0);
        if (mDialogMessageId == 0)
            return attrs.getAttributeValue(android_ns, key);
        else
            return context.getString(mDialogMessageId);
    }


    // ------------------------------------------------------------------------------------------
    // Constructor :
    public SeekBarPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Get string value for dialogMessage :
        mDialogMessage = getAttributeStringValue(context, attrs, "dialogMessage");
        // Get string value for suffix (text attribute in xml file) :
        mSuffix = getAttributeStringValue(context, attrs, "text");

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeIntValue(android_ns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(android_ns, "max", 100);
    }
    // ------------------------------------------------------------------------------------------


    // ------------------------------------------------------------------------------------------
    // DialogPreference methods :

    @Override
    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);

        TextView mSplashText = (TextView) view.findViewById(R.id.seekbar_splash_text);
        mValueText = (TextView) view.findViewById(R.id.seekbar_value_text);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar_seekbar);

        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore)
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        else
            mValue = (Integer) defaultValue;
    }
    // ------------------------------------------------------------------------------------------


    // ------------------------------------------------------------------------------------------
    // OnSeekBarChangeListener methods :
    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        String t = String.valueOf(value);
        mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek)
    {
    }

    // ------------------------------------------------------------------------------------------


    // ------------------------------------------------------------------------------------------
    // Set the positive button listener and onClick action : 
    @Override
    public void showDialog(Bundle state)
    {
        super.showDialog(state);

        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (shouldPersist())
        {
            mValue = mSeekBar.getProgress();
            persistInt(mValue);
            callChangeListener(mValue);
        }

        getDialog().dismiss();
    }
    // ------------------------------------------------------------------------------------------
}