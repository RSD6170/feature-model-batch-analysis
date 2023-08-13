import time
from func_timeout import func_timeout, FunctionTimedOut

from sat_instance.sat_instance import SATInstance


sat_inst = SATInstance(file_name, preprocess=True, verbose=True, preprocess_tmp=False)
timeout = remaining_time
if sat_inst.solved:
    retValue = {"presolved": True}
else:
    retValue = sat_inst.features_dict
    sat_inst.features_dict["presolved"]=False
    t1 = time.time()
    try:
        func_timeout(timeout, sat_inst.gen_basic_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(timeout))
    t2 = time.time()
    try:
        func_timeout(timeout, sat_inst.gen_dpll_probing_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(timeout))
    # linux only
    try:
        func_timeout(timeout, sat_inst.gen_local_search_probing_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(timeout))
    t3 = time.time()
    try:
        func_timeout(timeout, sat_inst.gen_ansotegui_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(timeout))
    t4 = time.time()
    try:
        func_timeout(timeout, sat_inst.gen_manthey_alfonso_graph_features)
    except FunctionTimedOut:
        print("Timed out after {} seconds".format(timeout))
    t5 = time.time()

    sat_inst.features_dict["satzilla_base_t"] = (t2 - t1)
    sat_inst.features_dict["satzilla_probe_t"] = (t3 - t2)
    sat_inst.features_dict["ansotegui_t"] = (t4 - t3)
    sat_inst.features_dict["alfonso_t"] = (t5 - t4)
