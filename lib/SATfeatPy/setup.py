from setuptools import setup

setup(
    name='SATfeatPy',
    version='1.0',
    packages=['sat_instance', 'feature_computation', 'api'],
    url='https://github.com/bprovanbessell/SATfeatPy',
    license='CC-BY 4.0',
    author='Ben Provan-Bessel, Marco Dalla',
    author_email='b.provanbessell@cs.ucc.ie',
    description='',
    install_requires=[
        'networkx',
        'python-louvain',
        'powerlaw',
        'scikit-learn',
        'scipy'
    ]
)
