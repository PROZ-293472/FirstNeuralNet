public class Executable {
    public static void main(String args[]){
        NeuralNet net = new NeuralNet(5,4,6,10, 0.3);
        net.loadInput("data.txt");
        net.showNet();
        Double[] inputs = new Double[]{0.1, 0.5, 0.2, 0.9, 0.7};
        Double[] target = new Double[]{0.0, 0.0, 1.0, 0.0, 1.0, 0.0};


        for(int i = 0; i< 2000; i++){
            net.train("data.txt", "targets.txt");
        }
        net.printOutput();


    }
}
