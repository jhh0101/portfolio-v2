rootProject.name = "auction-v2"

include("auction-common")
include("auction-user-service")

include("auction-seller-service")

include("auction-auction-service")
include("auction-product-service")
include("auction-category-service")

include("auction-rating-service")
include("auction-order-service")

include("auction-bid-service")

include("auction-app")

// 하위 모듈
include("auction-user-api")
include("auction-auction-api")
include("auction-product-api")
include("auction-seller-api")
include("auction-bid-api")
include("auction-order-api")
include("auction-category-api")