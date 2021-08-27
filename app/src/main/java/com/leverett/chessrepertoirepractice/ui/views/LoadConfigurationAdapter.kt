package com.leverett.chessrepertoirepractice.ui.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.leverett.chessrepertoirepractice.R
import com.leverett.repertoire.chess.RepertoireManager

class LoadConfigurationAdapter(val dismiss: () -> Unit, val repertoireViewUpdate: () -> Unit):
    RecyclerView.Adapter<LoadConfigurationAdapter.ViewHolder>() {

    private val repertoireManager = RepertoireManager

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.configuration_name)
        val deleteView: AppCompatImageView = view.findViewById(R.id.delete_configuration_button)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.configuration_option, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = repertoireManager.configurations.keys.toList()[position]
        viewHolder.textView.setOnClickListener{
            repertoireManager.loadConfiguration(repertoireManager.configurations.keys.toList()[position])
            repertoireViewUpdate()
            dismiss()
        }
        // TODO delete confirmation
        viewHolder.deleteView.setOnClickListener {
            repertoireManager.deleteConfiguration(viewHolder.textView.text.toString())
            if (repertoireManager.configurations.isEmpty()) {
                dismiss()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = repertoireManager.configurations.size
}