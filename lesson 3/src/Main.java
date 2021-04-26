public class Main {

    private final Object monitor = new Object();
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {

        Main waitNotifyObject = new Main();

        Thread thread1 = new Thread(waitNotifyObject::printA);

        Thread thread2 = new Thread(waitNotifyObject::printB);

        Thread thread3 = new Thread(waitNotifyObject::printC);

        thread1.start();
        thread2.start();
        thread3.start();

    }

    public void printA() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++){
                    while (currentLetter != 'A'){
                        monitor.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    monitor.notifyAll();
                }
            } catch (InterruptedException exception){
                exception.printStackTrace();
            }
        }
    }

    public void printB() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++){
                    while (currentLetter != 'B'){
                        monitor.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    monitor.notifyAll();
                }
            } catch (InterruptedException exception){
                exception.printStackTrace();
            }
        }
    }

    public void printC() {
        synchronized (monitor) {
            try {
                for (int i = 0; i < 5; i++){
                    while (currentLetter != 'C'){
                        monitor.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    monitor.notifyAll();
                }
            } catch (InterruptedException exception){
                exception.printStackTrace();
            }
        }
    }

}
