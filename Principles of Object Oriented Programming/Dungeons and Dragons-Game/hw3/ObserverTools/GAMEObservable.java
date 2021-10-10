package ObserverTools;
import java.util.LinkedList;
import java.util.List;

public class GAMEObservable {

    List<GAMEObserver> list = new LinkedList<GAMEObserver>();

    public void Register(GAMEObserver g)
    {
        list.add(g);
    }

    public void NotifyMessage(String message)
    {
        for (GAMEObserver g:list)
            g.print(message);
    }
}
