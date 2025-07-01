package ddwucom.mobile.medikeeper.data.pharm_ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwucom.mobile.medikeeper.data.pharm.network.ModelProperties
import ddwucom.mobile.medikeeper.databinding.ListPharmBinding
class PharmacyAdapter(
    private var pharmacies: List<ModelProperties>,
    private val onItemClick: (ModelProperties) -> Unit
) : RecyclerView.Adapter<PharmacyAdapter.PharmacyHolder>() {

    override fun getItemCount(): Int = pharmacies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyHolder {
        val itemBinding = ListPharmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PharmacyHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PharmacyHolder, position: Int) {
        val pharmacy = pharmacies[position]
        holder.bind(pharmacies[position])
        holder.itemView.setOnClickListener{
            onItemClick(pharmacy)
        }
    }

    fun setData(newPharmacies: List<ModelProperties>) {
        pharmacies = newPharmacies
        notifyDataSetChanged()
    }

    inner class PharmacyHolder(private val itemBinding: ListPharmBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(pharmacy: ModelProperties) {
            itemBinding.tvPharmacyName.text = pharmacy.pharmacyName
            itemBinding.tvAddress.text = pharmacy.address
            itemBinding.tvPhoneNumber.text = pharmacy.phoneNumber

            // 클릭 이벤트 설정
            itemBinding.root.setOnClickListener {
                onItemClick(pharmacy)
            }

            itemBinding.tvPhoneNumber.setOnClickListener{
                val phoneNumber = pharmacy.phoneNumber.replace("-", "")
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                itemView.context.startActivity(intent)
            }
        }
    }
}
