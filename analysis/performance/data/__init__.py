from pathlib import Path

import pandas as pd

data_dir = Path(__file__).parent

csv_file = data_dir / 'HierarchicalGridDiv100Max10Tests' / '2023-11-29-21-45-01_index_Random_Linear.csv'

csv_data = pd.read_csv(csv_file)
