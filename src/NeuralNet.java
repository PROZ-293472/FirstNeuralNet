import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class NeuralNet {
    private ArrayList layers = new ArrayList<ArrayList<Neuron>>();
    private ArrayList<Neuron> inputs = new ArrayList<Neuron>();
    private ArrayList<Neuron> outputs = new ArrayList<Neuron>();
    private Double etha;  //learning rate
    // private Double[][] errors; //TODO <- hell idk how this should look like. Maybe try to use it as a field of each neuron in Neuron class? :0
    //TODO ^ that did it mate

    public NeuralNet(/*topology of some kind*/ int numOfInputs, int numOfHiddenLayers, int numOfOutputs, int neuronsInLayer, Double etha) {

        this.etha = etha;

        for (int iIndex = 0; iIndex < numOfInputs; iIndex++) {
            this.inputs.add(new Neuron(iIndex));
        }

        this.layers.add(inputs);
        for (int currLayer = 0; currLayer < numOfHiddenLayers; currLayer++) {
            ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
            for (int hiddenNeuronIndex = 0; hiddenNeuronIndex < neuronsInLayer; hiddenNeuronIndex++) {
                hiddenLayer.add(new Neuron(hiddenNeuronIndex));
            }
            this.layers.add(hiddenLayer);
        }
        for (int oIndex = 0; oIndex < numOfOutputs; oIndex++) {
            this.outputs.add(new Neuron(oIndex));
        }
        this.layers.add(outputs);


        //****INIT****//
        updateAll();
    }

    private void updateAll() {
        for (int l = 0; l < this.layers.size(); l++) {
            ArrayList<Neuron> currentLayer = (ArrayList<Neuron>) this.layers.get(l);
            for (Neuron neuron : currentLayer) {
                if (l != 0 && l != this.layers.size() - 1)
                    neuron.initialize((ArrayList<Neuron>) this.layers.get(l - 1), (ArrayList<Neuron>) this.layers.get(l + 1));
                else if (l == 0) {
                    neuron.initialize(null, (ArrayList<Neuron>) this.layers.get(l + 1));
                } else if (l == this.layers.size() - 1) {
                    neuron.initialize((ArrayList<Neuron>) this.layers.get(l - 1), null);
                }
            }
        }
    }


    /*
    ***********************
    * BACKPROP FORMULAS****
    * *********************
    TODO: dE/dw_ij = dE/do_j * do_j/dnet_j * dnet_j/dw_ij
    E - squared error
    w_ij - weight of i wage in each neuron of j layer
    o_j - value of each neuron in j layer
    net_j - sum of wages and values (aka neuron without sigmoid function)

    dnet_j/dw_ij = o_i

    do_j/dnet_j = sigmoid(net_j)(1-sigmoid(net_j)

    dE/do_j = y - t
    y - actual output of a neuron
    t - target output of a neuron

    TODO FINAL FORMULA: dE/dw_ij = delta_j*o_j
    delta_j =
    1) for j - output neuron: (o_j - t_j)*o_j*(1-o_j)
    2) for j - inner neuron: (sum by l in L (w_jl*delta_l))*o_j*(1-o_j)
    Where L is a set of all neurons receiving input from j

    NOTE: delta is a recursive function!
    */

    private void backPropErrors(ArrayList<Double> targets) {

        //first start with the output layer
        for (Neuron neuron : this.outputs) {
            Double value = neuron.getValue();
            neuron.error = (value - targets.get(neuron.layerIndex)) * value * (1 - value);
        }

        //now for the rest of neurons that rely on the "previous" (closer to the outputs) layer
        for (int i = layers.size() - 2; i > 0; i--) { //TODO Check if it's not out of border
            for (Neuron neuron : (ArrayList<Neuron>) layers.get(i)) {
                Double value = neuron.getValue();
                Double sum = 0.0;
                for (Neuron nextNeuron : (ArrayList<Neuron>) layers.get(i + 1)) { //TODO Really man. Check this
                    sum += neuron.outputWages.get(nextNeuron.layerIndex) * nextNeuron.error;
                }
                neuron.error = sum * value * (1 - value);
            }
        }
    }

    public void updateWeights() {
        for (int i = 1; i < this.layers.size(); i++) {
            ArrayList<Neuron> currLayer = (ArrayList<Neuron>) layers.get(i);
            ArrayList<Neuron> prevLayer = (ArrayList<Neuron>) layers.get(i - 1); //I don't know chief, looks sketchy
            for (Neuron currNeuron : currLayer) {
                for (Neuron prevNeuron : prevLayer) {
                    Double correction = -this.etha * prevNeuron.getValue() * currNeuron.error;
                    Double temp = prevNeuron.outputWages.get(currNeuron.layerIndex);
                    temp += correction;
                    prevNeuron.outputWages.set(currNeuron.layerIndex, temp); //Clumsy as hell, but you gotta do what you gotta do
                }
                Double biasCorrection = -this.etha * currNeuron.error;
                currNeuron.bias += biasCorrection;
            }
        }
    }

    //------------------FOR TEST PURPOSES ------------------------//
    public void testTrain(Double[] inputs, Double[] outputs) {

        if (inputs.length != this.inputs.size() || outputs.length != this.outputs.size()) return;
        for (int i = 0; i < inputs.length; i++) {
            this.inputs.get(i).setValue(inputs[i]);
        }
        ArrayList<Double> targets = new ArrayList<>();
        for (int i = 0; i < outputs.length; i++) {
            targets.add(outputs[i]);
        }
        updateAll();
        backPropErrors(targets);
        updateWeights();
    }

    //----------------------FILE SYSTEM---------------------------//
    //Assumption - data portion matches the input and target values
    //Assumption - there are three separate files: inputs, targets and results (results will be created during process)
    public void loadInput(String filePath) {
        try {
            FileReader fin = new FileReader(filePath);
            Scanner src = new Scanner(fin);

            for (Neuron neuron : this.inputs) {
                if (src.hasNextDouble()) {
                    Double temp = src.nextDouble();
                    neuron.setValue(temp);
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.out.println("File not found");
        }
    }

    private ArrayList<Double> getTarget(String filePath){
        try {
            FileReader fin = new FileReader(filePath);
            Scanner src = new Scanner(fin);
            ArrayList<Double> targets = new ArrayList<>();

            for (int i = 0; i < this.outputs.size(); i++) {
                if(src.hasNextDouble()){
                    targets.add(src.nextDouble());
                }
            }
            return targets;
        }catch (FileNotFoundException fnfe){
            System.out.println("File not found");
            return  null;
        }
    }

    public void getResult(String filePath) {
        try {
            FileWriter fout = new FileWriter(filePath);
            fout.write(outputToString());
            fout.close();
        }catch (IOException ioe){
            System.out.println("Error with writing to file");
        }
    }


    public void train(String inputFilePath, String targetFilePath) {
        loadInput(inputFilePath);
        ArrayList<Double> target = getTarget(targetFilePath);
        updateAll();
        backPropErrors(target);
        updateWeights();
    }
    /*--------------------utils----------------------------------*/

    public void showNet() {
        for (ArrayList<Neuron> layer : (ArrayList<ArrayList<Neuron>>) this.layers) {
            for (Neuron neuron : layer) {
                System.out.println(neuron.layerIndex + " = value: " + neuron.getValue() + " bias: " + neuron.bias + "\n");
            }
            System.out.println("-----");
        }
    }



    private String outputToString(){
        String str = "The output is: ";
        for (Neuron neuron : this.outputs) {
            str += neuron.getValue() + " ";
        }
        return str;
    }


    public void printOutput() {
        String printMe = "The output is: ";
        for (Neuron neuron : this.outputs) {
            printMe += neuron.getValue() + " ";
        }
        System.out.println(printMe);
    }

}
