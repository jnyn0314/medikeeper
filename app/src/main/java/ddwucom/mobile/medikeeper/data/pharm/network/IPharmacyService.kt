package ddwucom.mobile.medikeeper.data.pharm.network
import retrofit2.http.GET
import retrofit2.http.Query

interface IPharmacyService {

    // @GET https://api.odcloud.kr/api/15065023/v1/uddi:cbae0311-056a-4991-ba08-f791f123971c?page=1&perPage=10&serviceKey=inOyAAod5g8EW7TFB3ZMEo8vSoY9kWcd7sPICeRLoho6T2%2FJNauUWcBoZ7apsJw4S1XyazrtM67aEP96pRhjIA%3D%3D
    // @GET: https://api.odcloud.kr/api/15065023/v1/uddi:cbae0311-056a-4991-ba08-f791f123971c
    // @Query serviceKey
    // @Query page
    // @Query perPage

    @GET("api/15065023/v1/uddi:cbae0311-056a-4991-ba08-f791f123971c")
    suspend fun getPharmacy(
        @Query("serviceKey") serviceKey: String,      // API 인증 키
        @Query("page") page: Int,                // 페이지 번호
        @Query("perPage") perPage: Int          // 한 페이지당 데이터 개수
    ): Pharamcy
}