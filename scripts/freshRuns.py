"""
Author: Juan Fumero
Email : juan.fumero@ed.ac.uk
"""

import os

SCRIPT="./runbench "
URL="benchmarks/fastR/"
DIRNAME = "logsSizes"
DEBUG = False

NUMBER_OF_FRESH_RUNS=3

def createDirectory(directoryName):
    if not os.path.exists(directoryName):
        os.makedirs(directoryName)

def execute(bench, sizes, version, symName):
    print "\nRunning: " + version
    logFile = DIRNAME + "/" + symName 
    for s in sizes:
        for i in range(NUMBER_OF_FRESH_RUNS):
            command = SCRIPT + URL + bench + "/" + version + " " + str(s) + " > " + logFile + str(s) + ".log." + str(i)
            print "\t" + command
            if (not DEBUG):
                os.system(command)

def runExperiment(bench, sizes, versions, symbolicNames):
    for i in range(len(versions)):
        version = versions[i]
        symbolicName = symbolicNames[i]
        execute(bench, sizes, version, symbolicName)

def saxpy():
    bench="saxpy"

    mainSize = 8388608
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["saxpy.R", "saxpyGPU.R", "saxpyGPUPArrays"]
    symbolicNames = ["saxpyFastR", "saxpyASTxFull", "saxpyASTx"]

    runExperiment(bench, sizes, versions, symbolicNames)

def blacksholes():
    bench = "blacksholes"
    
    mainSize = 1048576
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["blackcholes.R",  "blackcholesGPU.R", "blackcholesGPUPArrays"]
    symbolicNames = ["blackcholesFastR", "blackcholesASTxFull", "blackcholesASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)

def nbody():
    bench = "nbody"

    mainSize = 65536
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["nbody.R", "nbodyGPU.R", "nbodyGPUPArrays"]
    symbolicNames = ["nbodyFastR", "nbodyASTxFull", "nbodyASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)

def dft():
    bench = "dft"

    mainSize = 8192
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["dft.R",  "dftGPU.R", "dftGPUPArrays"]
    symbolicNames = ["dftFastR", "dftASTxFull", "dftASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)
 
def mandelbrot():
    bench = "mandelbrot"
    mainSize = 1024
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    sizes = [1, 2, 3, 4, 5]

    versions = ["mandelbrot.R",  "mandelbrotGPU.R", "mandelbrotGPUPArrays"]
    symbolicNames = ["mandelbrotFastR", "mandelbrotASTxFull", "mandelbrotASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)

def kmeans():
    bench = "kmeans"
    mainSize = 4194304
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["kmeans.R", "kmeansGPU.R", "kmeansGPUPArrays"]
    symbolicNames = ["kmeansFastR", "kmeansASTxFull", "kmeansASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)
 
def hilbert():
    bench = "hilbert"
    mainSize = 4096
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["hilbert.R", "hilbertGPU.R", "hilbertGPUPArrays"]
    symbolicNames = ["hilbertFastR", "hilbertASTxFull", "hilbertASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)
 
def spectralNorm():
    bench = "spectralNorm"
    mainSize = 32768
    sizes = [mainSize/4, mainSize/2, mainSize, mainSize*2, mainSize*4]

    versions = ["spectralNorm.R", "spectralNormGPU.R", "spectralNormGPUPArrays"]
    symbolicNames = ["spectralNormFastR",  "spectralNormASTxFull", "spectralNormASTx"]
    
    runExperiment(bench, sizes, versions, symbolicNames)
 
if __name__ == "__main__":

    createDirectory(DIRNAME)
    
    saxpy()
    blacksholes()   
    nbody()
    dft()
    mandelbrot()
    kmeans()
    hilbert()
    spectralNorm()

