from pathlib import Path
import numpy as np
import pandas as pd

data_dir = Path(__file__).parent

#csv_file = data_dir / 'HierarchicalGridDiv100Max10Tests' / '2023-11-29-21-45-01_index_Random_Linear.csv'

#csv_data = pd.read_csv(csv_file)




# Getting a data frame with all the data:
# https://stackoverflow.com/a/21232849/231860

# Get the files from the path provided in the OP
files = Path(data_dir).rglob('*.csv')  # .rglob to get subdirectories


dfs = list()
for f in files:
    data = pd.read_csv(f)
    # .stem is method for pathlib objects to get the filename w/o the extension
    data['file'] = f.stem
    dfs.append(data)

csv_data = pd.concat(dfs, ignore_index=True)

