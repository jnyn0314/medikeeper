package ddwucom.mobile.medikeeper

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwucom.mobile.medikeeper.data.pill.Pill
import ddwucom.mobile.medikeeper.databinding.ListItemBinding

class PillAdapter(
    private var pills: List<Pill>
) : RecyclerView.Adapter<PillAdapter.PillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        val pill = pills[position]
        holder.binding.tvText.text = pill.name // 약 이름 설정
    }

    override fun getItemCount(): Int = pills.size

    fun updateData(newPills: List<Pill>) {
        pills = newPills
        Log.d("PillAdapter", "Data updated: ${pills.size}") // 데이터 크기 확인
        notifyDataSetChanged()
    }


    inner class PillViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
