package ddwucom.mobile.medikeeper.data.pharm_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ddwucom.mobile.medikeeper.data.pharm.RefRepository
class RefViewModelFactory(private val refRepository: RefRepository) : ViewModelProvider.Factory {
    // ViewModel 객체를 생성하는 함수를 재정의
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 생성하려는 클래스가 FoodViewModel 일 경우 객체 생성
        if (modelClass.isAssignableFrom(RefViewModel::class.java)) {
            return RefViewModel(refRepository) as T
        }
        return IllegalArgumentException("Unknown ViewModel class") as T
    }
}