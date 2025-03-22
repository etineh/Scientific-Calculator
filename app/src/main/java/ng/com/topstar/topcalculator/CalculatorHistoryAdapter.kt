package ng.com.topstar.topcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalculatorHistoryAdapter(
    private var historyList: MutableList<CalculatorHistoryM>,
    private val onItemClicked: (CalculatorHistoryM) -> Unit
) : RecyclerView.Adapter<CalculatorHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_cal_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history)
        holder.bind(history)
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val inputTV: TextView = itemView.findViewById(R.id.inputTV)
        private val totalAnsTV: TextView = itemView.findViewById(R.id.totalAnsTV)
        private val dateTV: TextView = itemView.findViewById(R.id.dateTV)

        fun bind(historyM: CalculatorHistoryM) {
            inputTV.text = historyM.input
            dateTV.text = historyM.date.toDateExt()
            val ans = "= ${historyM.totalAns}"
            totalAnsTV.text = ans

            itemView.setOnClickListener {
                it.hideKeyboard()
                onItemClicked(historyM)
            }
        }

    }

    fun updateHistory(newHistory: CalculatorHistoryM) {
        historyList.add(newHistory)
        notifyDataSetChanged()
    }

}
