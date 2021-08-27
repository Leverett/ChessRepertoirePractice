package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.leverett.chessrepertoirepractice.R
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.*

class RepertoireListAdapter(deleteOption: Boolean = false): BaseExpandableListAdapter() {

    private val itemLayout = if (deleteOption) R.layout.repertoire_list_item_deletable else R.layout.repertoire_list_item
    private val repertoireManager = RepertoireManager
    private val bookToGroupView = mutableMapOf<Book, ConstraintLayout>()
    private val chapterToItemView = mutableMapOf<Chapter, ConstraintLayout>()
    private var isRefreshing = false

    override fun getGroupCount(): Int {
        return repertoireManager.repertoireSize
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val lineTree = getGroup(groupPosition)
        return if (lineTree is Book) lineTree.lineTrees.size else -1
    }

    override fun getGroup(groupPosition: Int): Any {
        return repertoireManager.getLineTree(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return (getGroup(groupPosition) as Book).lineTrees[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val lineTree = getGroup(groupPosition) as LineTree
        Log.e("getGroupView", lineTree.name)
        Log.e("getGroupView", repertoireManager.repertoire.lineTrees[0].name)

        val groupView = (parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(itemLayout, null) as ConstraintLayout
        val textView = groupView.findViewById<TextView>(R.id.repertoire_item_text_view)
        textView.text = lineTree.name
        val checkBox = groupView.findViewById<CheckBox>(R.id.repertoire_item_check_box)
        if (repertoireManager.isActiveLine(lineTree)) {
            checkBox.isChecked = true
        }
        if (lineTree is Book) {
            bookToGroupView[lineTree] = groupView
        }
        setOnCheckListener(checkBox, lineTree)
        val deleteButton = groupView.findViewById<AppCompatImageView>(R.id.delete_repertoire_item)
        if (deleteButton != null) {
            setGroupDeleteButtonListener(deleteButton, lineTree)
        }
        return groupView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val chapter = getChild(groupPosition, childPosition) as Chapter
        val childView = (parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(itemLayout, null) as ConstraintLayout
        val textView = childView.findViewById<TextView>(R.id.repertoire_item_text_view)
        textView.text = chapter.name
        val checkBox = childView.findViewById<CheckBox>(R.id.repertoire_item_check_box)
        val book = getGroup(groupPosition) as Book
        if (repertoireManager.isActiveLine(book) || repertoireManager.isActiveLine(chapter)) {
            checkBox.isChecked = true
        }
        chapterToItemView[chapter] = childView
        setOnCheckListener(checkBox, chapter)
        val deleteButton = childView.findViewById<AppCompatImageView>(R.id.delete_repertoire_item)
        if (deleteButton != null) {
            setChildDeleteButtonListener(deleteButton, book, chapter)
        }
        return childView
    }

    private fun setOnCheckListener(view: CheckBox, lineTree: LineTree) {
        view.setOnCheckedChangeListener { _, isChecked ->
            if (!isRefreshing) {
                if (isChecked) {
                    repertoireManager.addActiveLine(lineTree)
                } else {
                    repertoireManager.removeActiveLine(lineTree)
                }
                refreshListViewChecks()
            }
        }
    }

    private fun setGroupDeleteButtonListener(view: AppCompatImageView, lineTree: LineTree) {
        view.setOnClickListener {
            Log.e("setGroupDeleteButtonListener", "before delete")
            repertoireManager.deleteLineTree(lineTree)
            Log.e("setGroupDeleteButtonListener", "after delete")
            notifyDataSetChanged()
            Log.e("setGroupDeleteButtonListener", "after notifyDataSetChanged")
        }
    }

    private fun setChildDeleteButtonListener(view: AppCompatImageView, book: Book, chapter: Chapter) {
    view.setOnClickListener {
        Log.e("setChildDeleteButtonListener", "before delete")
        repertoireManager.deleteLineTree(book, chapter)
        Log.e("setChildDeleteButtonListener", "after delete")
        notifyDataSetChanged()
        Log.e("setChildDeleteButtonListener", "after notifyDataSetChanged")
    }
}

    fun refreshListViewChecks() {
        isRefreshing = true
        for (lineTreeEntry in bookToGroupView.entries) {
            lineTreeEntry.value.findViewById<CheckBox>(R.id.repertoire_item_check_box).isChecked =
                repertoireManager.isActiveLine(lineTreeEntry.key)
        }
        for (lineTreeEntry in chapterToItemView.entries) {
            lineTreeEntry.value.findViewById<CheckBox>(R.id.repertoire_item_check_box).isChecked =
                (repertoireManager.isActiveLine(lineTreeEntry.key) ||
                        repertoireManager.isActiveLine(lineTreeEntry.key.book!!))
        }
        isRefreshing = false
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }


}