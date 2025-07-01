package ddwucom.mobile.medikeeper.data.pharm_ui
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ddwucom.mobile.medikeeper.data.pharm.RefRepository
import ddwucom.mobile.medikeeper.data.pharm.network.ModelProperties
import kotlinx.coroutines.launch

class RefViewModel (val refRepository: RefRepository) : ViewModel(){
    private var _pharmacies = MutableLiveData<List<ModelProperties>?>()
    val pharmacies = _pharmacies

    fun getPharmacies(key: String) = viewModelScope.launch{
        var result : List<ModelProperties>?
        result = refRepository.getPharmacy(key)

        _pharmacies.value = result
    }

}