public class Executable {
    public static void main(String args[]){
        NeuralNet net = new NeuralNet(4,3,4,12, 0.3);
        //net.showNet();
        Double[] inputs = new Double[]{0.1, 0.5, 0.2, 0.9};
        Double[] target = new Double[]{0.0, 0.0, 0.0, 0.0};


        for(int i = 0; i< 2000; i++){
            net.train(inputs,target);
        }
        net.printOutput();
    }
}
