package com.leverett.chessrepertoirepractice.ui.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.leverett.chessrepertoirepractice.R
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.settings.Configuration


class ConfigurationListAdapter(private val context: Context):
    BaseAdapter() {

    private val repertoireManager = RepertoireManager
    private val configurationsMap: Map<String, Configuration>
        get() = repertoireManager.configurations
    private val configurations: List<Configuration>
        get() = configurationsMap.values.toList()
    override fun getCount(): Int {
        return configurations.size
    }

    override fun getItem(position: Int): Any {
        return configurations[position]
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val configuration = getItem(position) as Configuration
        val layout = if (configuration.color) {R.layout.configuration_item_white} else R.layout.configuration_item_black
        val result: View = LayoutInflater.from(context)
            .inflate(layout, parent, false)
        result.findViewById<TextView>(R.id.name).text = configuration.name
        return result
    }

    fun getPosition(configuration: Configuration): Int {
        return configurations.indexOf(configuration)
    }

}