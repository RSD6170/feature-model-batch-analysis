import time

from sat_instance.sat_instance import SATInstance


sat_inst = SATInstance(file_name, preprocess=True, verbose=True, preprocess_tmp=False)
if sat_inst.solved:
    retValue = {}
else:
    t1 = time.time()
    sat_inst.gen_basic_features()
    t2 = time.time()
    sat_inst.gen_dpll_probing_features()
    # linux only
    sat_inst.gen_local_search_probing_features()
    t3 = time.time()
    sat_inst.gen_ansotegui_features()
    t4 = time.time()
    sat_inst.gen_manthey_alfonso_graph_features()
    t5 = time.time()

    sat_inst.features_dict["satzilla_base_t"] = (t2 - t1)
    sat_inst.features_dict["satzilla_probe_t"] = (t3 - t2)
    sat_inst.features_dict["ansotegui_t"] = (t4 - t3)
    sat_inst.features_dict["alfonso_t"] = (t5 - t4)
    retValue = sat_inst.features_dict