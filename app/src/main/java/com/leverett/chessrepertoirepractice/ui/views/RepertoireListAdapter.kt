package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.leverett.chessrepertoirepractice.R
import com.leverett.chessrepertoirepractice.utils.deleteLineTreeFile
import com.leverett.chessrepertoirepractice.utils.makeConfirmationDialog
import com.leverett.chessrepertoirepractice.utils.storeConfigurations
import com.leverett.chessrepertoirepractice.utils.storeRepertoire
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.RepertoireManager.printConfigurationRepertoires
import com.leverett.repertoire.chess.lines.*
import com.leverett.rules.chess.representation.log

class RepertoireListAdapter(private val context: Context,
                            private val layoutInflater: LayoutInflater,
                            private val repertoireListView: ExpandableListView,
                            private val selectAllView: CheckBox): BaseExpandableListAdapter() {

    private val itemLayout = R.layout.repertoire_list_item
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
        setGroupDeleteButtonListener(deleteButton, lineTree)
        groupView.setBackgroundColor(ContextCompat.getColor(groupView.context,  R.color.purple_200))
        return groupView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val chapter = getChild(groupPosition, childPosition) as Chapter
        val childView = (parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(itemLayout, null) as ConstraintLayout
        val textView = childView.findViewById<TextView>(R.id.repertoire_item_text_view)
        textView.text = chapter.chapterName
        textView.gravity = Gravity.CENTER_HORIZONTAL
        val checkBox = childView.findViewById<CheckBox>(R.id.repertoire_item_check_box)
        val book = getGroup(groupPosition) as Book
        if (repertoireManager.isActiveLine(book) || repertoireManager.isActiveLine(chapter)) {
            checkBox.isChecked = true
        }
        chapterToItemView[chapter] = childView
        setOnCheckListener(checkBox, chapter)
        val deleteButton = childView.findViewById<AppCompatImageView>(R.id.delete_repertoire_item)
        setChildDeleteButtonListener(deleteButton, book, chapter)
        return childView
    }

    private fun setOnCheckListener(view: CheckBox, lineTree: LineTree) {
        view.setOnCheckedChangeListener { _, isChecked ->
//            log("onCheckListener", printConfigurationRepertoires())
            if (!isRefreshing) {
                if (isChecked) {
                    repertoireManager.addActiveLine(lineTree)
                    selectAllView.isChecked = repertoireManager.isFullRepertoire()

                } else {
                    repertoireManager.removeActiveLine(lineTree)
                    selectAllView.isChecked = false
                }
                refreshListViewChecks()
            }
            printConfigurationRepertoires()
            storeConfigurations(context)
        }
    }

    private fun setGroupDeleteButtonListener(view: AppCompatImageView, lineTree: LineTree) {
        view.setOnClickListener {
            makeConfirmationDialog(context, layoutInflater, repertoireListView, "Delete ${lineTree.name}?") {
                deleteGroup(lineTree)
            }
        }
    }

    private fun deleteGroup(lineTree: LineTree) {
        val deletePosition = repertoireManager.repertoire.lineTrees.indexOf(lineTree)
        for (i: Int in deletePosition until repertoireManager.repertoireSize) {
            if (repertoireListView.isGroupExpanded(i + 1)) {
                repertoireListView.expandGroup(i)
            } else {
                repertoireListView.collapseGroup(i)
            }
        }
        repertoireManager.deleteLineTree(lineTree)
        deleteLineTreeFile(context, lineTree)
        storeRepertoire(context)
        notifyDataSetChanged()
    }

    private fun setChildDeleteButtonListener(view: AppCompatImageView, book: Book, chapter: Chapter) {
        view.setOnClickListener {
            makeConfirmationDialog(context, layoutInflater, repertoireListView, "Delete ${chapter.name}?") {
                deleteChild(book, chapter)
            }
        }
    }

    private fun deleteChild(book: Book, chapter: Chapter) {
        val lineTree = repertoireManager.deleteLineTree(book, chapter)
        if (lineTree != null) {
            deleteLineTreeFile(context, lineTree)
        }
        storeRepertoire(context)
        notifyDataSetChanged()
    }

    private fun refreshListViewChecks() {
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