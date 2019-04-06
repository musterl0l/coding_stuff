
def translate_mnist(mnist, n_trans=40):
    """
    Takes a DataSets object loaded using
    tensorflow.examples.tutorials.mnist.input_data.read_data_sets
    and replaces the 28x28 images with images of size 40x40.

    :param mnist: DataSets
                    MNIST data loaded using TensorFlow
    :return: None
    """
    for name in ('train', 'validation', 'test'):
        images = getattr(mnist, name).images.reshape((-1, 28, 28))
        n_ims = images.shape[0]
        images_ = np.zeros((n_ims, n_trans, n_trans), dtype=images.dtype)
        trans = np.random.choice(n_trans-28, size=(n_ims, 2))
        for i in range(n_ims):
            x, y = trans[i, :]
            images_[i, x: x+28, y: y+28] = images[i, ...]
        getattr(mnist, name)._images = \
            images_.reshape((n_ims,np.product(images_.shape[1:])))
