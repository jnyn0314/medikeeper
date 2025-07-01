package ddwucom.mobile.medikeeper.data.pharm.network
import com.google.gson.annotations.SerializedName

data class Pharamcy(
    @SerializedName("currentCount")
    val currentCount: Int,             // 반환된 데이터 개수
    @SerializedName("data")
    val data: List<ModelProperties>,   // 약국 정보 리스트
    @SerializedName("matchCount")
    val matchCount: Int,               // 검색된 데이터 개수
    @SerializedName("page")
    val page: Int,                     // 현재 페이지 번호
    @SerializedName("perPage")
    val perPage: Int,                  // 페이지당 데이터 개수
    @SerializedName("totalCount")
    val totalCount: Int                // 전체 데이터 수
)
data class ModelProperties(
    @SerializedName("약국명")
    val pharmacyName: String,
    @SerializedName("주소")
    val address: String,
    @SerializedName("약국전화번호")
    val phoneNumber: String,
    @SerializedName("기준일자")
    val date: String,
    @SerializedName("위도")
    val latitude: String,          // 타입을 String으로 변경
    @SerializedName("경도")
    val longitude: String,         // 타입을 String으로 변경
    @SerializedName("행정동")
    val dong: String
)
