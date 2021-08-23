package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.leverett.chessrepertoirepractice.R
import com.leverett.repertoire.chess.lines.*

class RepertoireListAdapter(private val repertoire: Repertoire, private val activeRepertoire: LineTreeSet): BaseExpandableListAdapter() {

    private val bookToGroupView = mutableMapOf<Book, ConstraintLayout>()
    private val chapterToItemView = mutableMapOf<Chapter, ConstraintLayout>()
    private var isRefreshing = false

    override fun getGroupCount(): Int {
        return repertoire.lineTrees.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val lineTree = getGroup(groupPosition)
        return if (lineTree is Book) lineTree.lineTrees.size else -1
    }

    override fun getGroup(groupPosition: Int): Any {
        return repertoire.lineTrees[groupPosition]
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
        return if (convertView == null) {
            val lineTree = getGroup(groupPosition) as LineTree
            val groupView = (parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.repertoire_list_item_layout, null) as ConstraintLayout
            val textView = groupView.findViewById<TextView>(R.id.repertoire_item_text_view)
            textView.text = lineTree.name
            val checkBox = groupView.findViewById<CheckBox>(R.id.repertoire_item_check_box)
            if (activeRepertoire.lineTrees.contains(lineTree)) {
                checkBox.isChecked = true
            }
            if (lineTree is Book) {
                bookToGroupView[lineTree] = groupView
                setBookOnCheckListener(checkBox, lineTree)
            } else {
                setChapterOnCheckListener(checkBox, lineTree as Chapter)
            }
            groupView
        } else {
            convertView
        }
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        return if (convertView == null) {
            val chapter = getChild(groupPosition, childPosition) as Chapter
            val childView = (parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.repertoire_list_item_layout, null) as ConstraintLayout
            val textView = childView.findViewById<TextView>(R.id.repertoire_item_text_view)
            textView.text = chapter.name
            val checkBox = childView.findViewById<CheckBox>(R.id.repertoire_item_check_box)
            val book = getGroup(groupPosition) as Book
            if (activeRepertoire.lineTrees.contains(book) || activeRepertoire.lineTrees.contains(chapter)) {
                checkBox.isChecked = true
            }
            chapterToItemView[chapter] = childView
            setChildChapterOnClickListener(checkBox, chapter, book)
            childView
        } else {
            convertView
        }
    }

    private fun setBookOnCheckListener(groupView: CheckBox, book: Book) {
        groupView.setOnCheckedChangeListener { _, isChecked ->
            if (!isRefreshing) {
                if (isChecked) {
                    activeRepertoire.lineTrees.add(book)
                } else {
                    activeRepertoire.lineTrees.remove(book)
                }
                for (chapter in book.lineTrees) {
                    activeRepertoire.lineTrees.remove(chapter)
                }
                refreshListViewChecks()
            }
        }
    }

    private fun setChapterOnCheckListener(groupView: CheckBox, chapter: Chapter) {
        groupView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activeRepertoire.lineTrees.add(chapter)
            } else {
                activeRepertoire.lineTrees.remove(chapter)
            }
        }
    }

    private fun setChildChapterOnClickListener(childView: CheckBox, chapter: Chapter, book: Book) {
        childView.setOnCheckedChangeListener { _, isChecked ->
            if (!isRefreshing) {
                if (isChecked) {
                    activeRepertoire.lineTrees.add(chapter)
                    if (activeRepertoire.lineTrees.containsAll(book.lineTrees)) {
                        activeRepertoire.lineTrees.removeAll(book.lineTrees)
                        activeRepertoire.lineTrees.add(book)
                    }
                } else {
                    if (activeRepertoire.lineTrees.contains(chapter)) {
                        activeRepertoire.lineTrees.remove(chapter)
                    } else {
                        activeRepertoire.lineTrees.remove(book)
                        for (lineTree in book.lineTrees) {
                            if (lineTree != chapter) {
                                activeRepertoire.lineTrees.add(lineTree)
                            }
                        }
                    }
                }
                refreshListViewChecks()
            }
        }
    }

    private fun refreshListViewChecks() {
        isRefreshing = true
        for (lineTreeEntry in bookToGroupView.entries) {
            lineTreeEntry.value.findViewById<CheckBox>(R.id.repertoire_item_check_box).isChecked =
                activeRepertoire.lineTrees.contains(lineTreeEntry.key)
        }
        for (lineTreeEntry in chapterToItemView.entries) {
            lineTreeEntry.value.findViewById<CheckBox>(R.id.repertoire_item_check_box).isChecked =
                (activeRepertoire.lineTrees.contains(lineTreeEntry.key) ||
                        activeRepertoire.lineTrees.contains(lineTreeEntry.key.book!!))
        }
        isRefreshing = false
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }


}