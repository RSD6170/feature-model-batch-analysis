import os
import subprocess

def satelite_preprocess(cnf_path="cnf_examples/basic.cnf", timeout_satelite=90):
    cnf_path = os.path.join(os.getcwd(), cnf_path)
    temp_fn = cnf_path[0:-4] + "_preprocessed.cnf"
    satelite_path = os.path.join(os.getcwd(), "lib/SATfeatPy/SatELite/SatELite_v1.0_linux")
    try :
        satelite_command = [ satelite_path, cnf_path, temp_fn]
        p = subprocess.run(satelite_command, timeout=timeout_satelite)
    except subprocess.TimeoutExpired:
        print("Satelite timed out after {} seconds!".format(timeout_satelite))
        return False, ""
    return True, temp_fn

