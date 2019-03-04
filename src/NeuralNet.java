import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class NeuralNet {
    private ArrayList layers = new ArrayList<ArrayList<Neuron>>();
    private ArrayList<Neuron> inputs = new ArrayList<Neuron>();
    private ArrayList<Neuron> outputs = new ArrayList<Neuron>();

    public NeuralNet(/*topology of some kind*/ int numOfInputs, int numOfHiddenLayers, int numOfOutputs, int neuronsInLayer) {

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

    //Assumption - data portion matches the input and target values
    //Assumption - there are three separate files: inputs, targets and results (results will be created during process)
    private void loadInput(File file) {
        Scanner scanner;
        for (Neuron iNeuron : this.inputs) {
            try {
                scanner = new Scanner(file);

                while (scanner.hasNextDouble()) {
                    iNeuron.setValue(scanner.nextDouble());
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.out.println("Exception in loadInput");
            }
        }
    }

    public void getResult(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
                for (Neuron neuron : this.outputs) {
                    Double value = neuron.getValue();
                    dataOutputStream.writeDouble(value);
                    dataOutputStream.writeChar(' ');
                }

                dataOutputStream.writeChar('\n');
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.out.println("Error in write Double");
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Error in writing to file. No such file");
        }
    }



    public void feedForward(File file) {
        loadInput(file);
        updateAll();
    }

    private Double costFunction(ArrayList<Double> targetVals) {
        Double sum = 0.0;
        for(Neuron neuron: this.outputs){
            Double temp = (neuron.getValue() - targetVals.get(neuron.layerIndex));
            temp *= temp;
            sum+=temp;
        }
        sum = sum/this.outputs.size();
        return sum;
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
    private void delta(){}

    public void backPropagate(){}


    /*--------------------pomocnicze ----------------------------------*/

    public void showNet() {
        for (ArrayList<Neuron> layer : (ArrayList<ArrayList<Neuron>>) this.layers) {
            for (Neuron neuron : layer) {
                System.out.println(neuron.layerIndex + " = value: " + neuron.getValue() + " bias: " + neuron.bias + "\n");
            }
            System.out.println("-----");
        }
    }

}
