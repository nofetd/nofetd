package Singeltone;
import java.util.Random;

public class UserGenerator implements RandomGenerator {
    public int nextInt(int n){
        Random rnd = new Random();
      return rnd.nextInt(n);
    }

    @Override
    public void Start(String path) {

    }

    @Override
    public boolean hasNext() {
        return true;
    }
}
