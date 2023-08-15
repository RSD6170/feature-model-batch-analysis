import time
import tempfile
from func_timeout import func_timeout, FunctionTimedOut

from sat_instance.sat_instance import SATInstance


sat_inst = SATInstance(file_name, preprocess=True, verbose=True, timeout_satelite=satelite_timeout)
if sat_inst.solved:
    retValue = {"presolved": 1}
else:
    retValue = sat_inst.features_dict
    sat_inst.features_dict["presolved"]=0
    t1 = time.time()
    try:
        func_timeout(basic_timeout, sat_inst.gen_basic_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(basic_timeout))
    t2 = time.time()
    try:
        func_timeout(dpll_timeout, sat_inst.gen_dpll_probing_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(dpll_timeout))
    # linux only
    try:
        func_timeout(local_timeout, sat_inst.gen_local_search_probing_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(local_timeout))
    t3 = time.time()
    try:
        func_timeout(ansotegui_timeout, sat_inst.gen_ansotegui_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(ansotegui_timeout))
    t4 = time.time()
    try:
        func_timeout(alfonso_timeout, sat_inst.gen_manthey_alfonso_graph_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(alfonso_timeout))
    t5 = time.time()

    sat_inst.features_dict["satzilla_base_t"] = (t2 - t1)
    sat_inst.features_dict["satzilla_probe_t"] = (t3 - t2)
    sat_inst.features_dict["ansotegui_t"] = (t4 - t3)
    sat_inst.features_dict["alfonso_t"] = (t5 - t4)
