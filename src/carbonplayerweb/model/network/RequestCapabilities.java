package carbonplayerweb.model.network;

class RequestCapabilities {

   public RequestCapabilities(boolean acceptCL, boolean acceptBearer, boolean acceptFree) {
      this.acceptCL = acceptCL;
      this.acceptBearer = acceptBearer;
      this.acceptFree = acceptFree;
   }

   public static RequestCapabilities create (boolean acceptFree) {
      return new RequestCapabilities(false, false, acceptFree);
   }

   public static RequestCapabilities createCL (boolean acceptFree) {
      return new RequestCapabilities(true, false, acceptFree);
   }

   private final boolean acceptCL;
   private final boolean acceptBearer;
   private final boolean acceptFree;

   public final boolean getAcceptCL() {
      return this.acceptCL;
   }

   public final boolean getAcceptBearer() {
      return this.acceptBearer;
   }

   public final boolean getAcceptFree() {
      return this.acceptFree;
   }
}