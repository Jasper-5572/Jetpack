package com.android.jasper.framework.expansion

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.Html
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes

/**
 *@author   Jasper
 *@create   2020/6/19 11:12
 *@describe
 *@update
 */

/**
 * 给EditText设置字符串，并让光标移动到最后面
 * @receiver EditText
 * @param value String
 */
fun EditText.setString(value: String? = "") {
    this.text = value?.let {
        Editable.Factory.getInstance().newEditable(it)
    } ?: Editable.Factory.getInstance().newEditable("")
    this.setSelection(getString().length)
}

/**
 * 禁止复制粘贴
 * @receiver EditText
 */
fun EditText.stopCopyPaste(){
    this.isLongClickable = false
    this.setTextIsSelectable(false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.customInsertionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }
            override fun onDestroyActionMode(mode: ActionMode?) {
            }


        }
    }
}

/**
 * 获取焦点并弹出键盘
 * @receiver EditText
 */
fun EditText.setFocusShowSoftInput(){
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(this,0)
}
/**
 *
 * @receiver EditText
 * @param stringRes Int
 */
fun EditText.setString(@StringRes stringRes: Int) {
    this.setText(stringRes)
    this.setSelection(getString().length)
}

/**
 *
 * @receiver TextView
 * @param value String?
 */
fun TextView.setString(value: String? = "") {
    this.text = value?.let {
        Editable.Factory.getInstance().newEditable(it)
    } ?: Editable.Factory.getInstance().newEditable("")
}
/**
 *
 * @receiver TextView
 * @param stringRes Int
 */
fun TextView.setString(@StringRes stringRes: Int) {
    this.setText(stringRes)
}
/**
 *
 * @receiver TextView
 * @param value String?
 */
fun TextView.setHtml(value: String? = "") {
    this.text = value?.let {
        @Suppress("DEPRECATION")
        Html.fromHtml(it)
    } ?: Editable.Factory.getInstance().newEditable("")
}

/**
 * 判断TextView的字符串是否为空
 * @receiver TextView
 * @return Boolean
 */
fun TextView.textIsEmpty(): Boolean = (this.text?.toString())?.isEmpty() ?: true

/**
 * 判断TextView的字符串是否非空
 * @receiver TextView
 * @return Boolean
 */
fun TextView.textIsNotEmpty(): Boolean = (this.text?.toString())?.isNotEmpty() ?: false

/**
 * 获取TextView的text
 */
fun TextView.getString(): String = this.text?.toString() ?: ""