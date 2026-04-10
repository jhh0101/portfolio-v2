package auction.auctionuserapi.auth.annotation

@Target(AnnotationTarget.VALUE_PARAMETER) // 파라미터에만 붙이겠다
@Retention(AnnotationRetention.RUNTIME)   // 실행 중에 인식하겠다
annotation class LoginUser