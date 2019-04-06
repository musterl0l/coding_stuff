import numpy as np
from scipy import signal

def noise_w(sigma_n, N=1):
    return np.random.normal(0.0, sigma_n, N)


def noise_p(sigma_n, fs, N=1):
    wn = noise_w(sigma_n, N)
    s = np.fft.rfft(wn, axis=0)
    f = np.fft.rfftfreq(wn.shape[0], d=1.0 / fs)
    f[0] = 1
    pn_fft = s.T / f ** 0.5
    return np.fft.irfft(pn_fft.T, axis=0)


def alpha(sigma_n, fs, N=1):
    s = np.fft.rfft(noise_w(sigma_n, N))
    f = np.fft.rfftfreq(N, d=1.0 / fs)
    w = np.zeros_like(f)
    f_alpha_ind = (f >= 8) & (f < 13)
    w[f_alpha_ind] = signal.gaussian(np.sum(f_alpha_ind), np.sum(f_alpha_ind) / 10)
    alpha_fft = w * s
    return np.fft.irfft(alpha_fft)

