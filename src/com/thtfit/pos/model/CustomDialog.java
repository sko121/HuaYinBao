package com.thtfit.pos.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thtfit.pos.R;
import com.thtfit.pos.debug.DebugPrint;

public class CustomDialog extends Dialog
{
	/* debug */
	private static String TAG = "CustomDialog";
	public static final boolean isDebug = true;
	// public static final boolean isDebug = false;
	public DebugPrint LOG = new DebugPrint(isDebug, TAG);
	
	public CustomDialog(Context context)
	{
		super(context);
	}

	public CustomDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public static class Builder
	{
		private Context context;
		LayoutInflater inflater;
		View layout;

		private String title;
		private String message;
		private EditText editText;
		private String positiveButtonText;
		private String neutralButtonText;
		private String negativeButtonText;
		private View contentView;
		private ImageButton tipInMoneyImageButton;
		private ImageButton tipInPercentImageButton;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener neutralButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;
		private DialogInterface.OnClickListener tipInPercentButtonClickListener;
		private DialogInterface.OnClickListener tipInMoneyButtonClickListener;

		public Builder(Context context)
		{
			this.context = context;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.dialog_normal_layout, null);
		}

		public View getEditText()
		{
			editText = (EditText) layout.findViewById(R.id.editText);
			return editText;
		}

		public Builder setMessage(String message)
		{
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message)
		{
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title)
		{
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title)
		{
			this.title = title;
			return this;
		}

		public Builder setContentView(View v)
		{
			this.contentView = v;
			return this;
		}

		
		public Builder setTipInPercentButton(DialogInterface.OnClickListener listener)
		{
			this.tipInPercentImageButton = (ImageButton) layout.findViewById(R.id.tipInPercent);
			this.tipInPercentButtonClickListener = listener;
			return this;
		}
		public View getTipInPercentButton(){
			return this.tipInPercentImageButton;
		}
		public Builder setTipInMoneyButton(DialogInterface.OnClickListener listener)
		{
			this.tipInMoneyImageButton = (ImageButton) layout.findViewById(R.id.tipInMoney);
			this.tipInMoneyButtonClickListener = listener;
			return this;
		}
		public View getTipInMoneyButton(){
			return this.tipInMoneyImageButton;
		}
		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener)
		{
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener)
		{
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int neutralButtonText, DialogInterface.OnClickListener listener)
		{
			this.neutralButtonText = (String) context.getText(neutralButtonText);
			this.neutralButtonClickListener = listener;
			return this;

		}

		public Builder setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener)
		{
			this.neutralButtonText = neutralButtonText;
			this.neutralButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener)
		{
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener)
		{
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog create()
		{

			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);

			dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the tipInPercent button
			if (tipInPercentImageButton != null)
			{
				if (tipInPercentButtonClickListener != null)
				{
					((ImageButton) layout.findViewById(R.id.tipInPercent)).setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							tipInPercentButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			}
			else
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.tipInPercent).setVisibility(View.GONE);
			}
			
			// set the tipInMoney button
			if (tipInMoneyImageButton != null)
			{
				if (tipInMoneyButtonClickListener != null)
				{
					((ImageButton) layout.findViewById(R.id.tipInMoney)).setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							tipInMoneyButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			}
			else
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.tipInMoney).setVisibility(View.GONE);
			}

			// set the confirm button
			if (positiveButtonText != null)
			{
				((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
				if (positiveButtonClickListener != null)
				{
					((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
			}
			else
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
			}
			// set the center button
			if (neutralButtonText != null)
			{
				((Button) layout.findViewById(R.id.neutralButton)).setText(neutralButtonText);
				if (neutralButtonClickListener != null)
				{
					((Button) layout.findViewById(R.id.neutralButton)).setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							neutralButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
						}
					});
				}
			}
			else
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.neutralButton).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null)
			{
				((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
				if (negativeButtonClickListener != null)
				{
					((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener()
					{
						public void onClick(View v)
						{
							negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
			}
			else
			{
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null)
			{
				((TextView) layout.findViewById(R.id.message)).setText(message);
			}
			else if (contentView != null)
			{
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}

			if (editText != null)
			{
				((TextView) layout.findViewById(R.id.message)).setVisibility(View.GONE);
				editText.setVisibility(View.VISIBLE);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}
