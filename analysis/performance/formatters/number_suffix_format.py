from bisect import bisect_right

import numpy as np
from plotnine.utils import match


class number_suffix_format:
    """
    Number Formatter that shows k,M,G,T,P

    Parameters
    ----------
    symbol : str
        Valid symbols are "", "k", "M", "G", "T", "P", "E",
        "Z", and "Y" for SI units.
    units : "si"
        Which unit base to use, 1000 for "si".
    fmt : str, optional
        Format sting. Default is ``{:.0f}``.
    suffix : str, optional
        Suffix to add to the end of the string. Default is "".

    Examples
    --------
    >>> x = [1000, 1000000, 4e5]
    >>> number_suffix_format()(x)
    ['1000 B', '977 KiB', '391 KiB']
    >>> number_suffix_format(units='si')(x)
    ['1 kB', '1 MB', '400 kB']
    """

    def __init__(self, symbol="auto", units="si", fmt="{:.0f} ", suffix=""):
        self.symbol = symbol
        self.units = units
        self.fmt = fmt
        self.suffix = suffix;

        if units == "si":
            self.base = 1000
            self._all_symbols = [
                "",
                "k",
                "M",
                "G",
                "T",
                "P",
                "E",
                "Z",
                "Y",
            ]
        else:
            self.base = 1024
            self._all_symbols = [
                "",
                "K",
                "M",
                "G",
                "T",
                "P",
                "E",
                "Z",
                "Y",
            ]

        # possible exponents of base: eg 1000^1, 1000^2, 1000^3, ...
        exponents = np.arange(1, len(self._all_symbols) + 1, dtype=float)
        self._powers = self.base**exponents
        self._validate_symbol(symbol, ["auto"] + self._all_symbols)

    def __call__(self, x):
        _all_symbols = self._all_symbols
        symbol = self.symbol
        if symbol == "auto":
            power = [bisect_right(self._powers, val) for val in x]
            symbols = [_all_symbols[p] for p in power]
        else:
            power = np.array(match([symbol], _all_symbols))
            symbols = [symbol] * len(x)

        x = np.asarray(x)
        power = np.asarray(power, dtype=float)
        values = x / self.base**power
        fmt = (self.fmt + "{}{}").format
        labels = [fmt(v, s, self.suffix) for v, s in zip(values, symbols)]
        return labels

    def _validate_symbol(self, symbol, allowed_symbols):
        if symbol not in allowed_symbols:
            raise ValueError(
                "Symbol must be one of {}".format(allowed_symbols)
            )
