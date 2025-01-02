/* 
  * NOTE - Not from Google Hipster Shop
  * Not given with the original code
  * I added to have something to "Test" this will be expanded in the future
  * 
*/


// src/test/java/hipstershop/AdServiceTests.java
package hipstershop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdServiceTests {
	
  // Smoke test to that gradle tests are working
  @Test
	void contextLoads() {
	}

  /* -----  SAVING FOR LATER ---- 
  @Test
  void testGetInstance() {
      // This is a placeholder test that will always pass
      AdService instance = AdService.getInstance();
      assertNotNull(instance); // Basic assertion to ensure instance is not null
  }

  @Test
  void testGetRandomAds() {
      // This is a placeholder test that will always pass
      AdService instance = AdService.getInstance();
      List<Ad> ads = instance.getRandomAds();
      assertNotNull(ads); // Basic assertion to ensure the list is not null
  }

  @Test
  void testAdServiceImplGetAds() {
      // This is a placeholder test that will always pass
      AdService.AdServiceImpl serviceImpl = new AdService.AdServiceImpl();
      AdRequest request = AdRequest.newBuilder().build();
      StreamObserver<AdResponse> responseObserver = new StreamObserver<AdResponse>() {
          @Override
          public void onNext(AdResponse adResponse) {
              assertNotNull(adResponse); // Basic assertion to ensure response is not null
          }

          @Override
          public void onError(Throwable throwable) {
              fail("Request failed");
          }

          @Override
          public void onCompleted() {
              // Do nothing
          }
      };
      serviceImpl.getAds(request, responseObserver);
  }
  */
}