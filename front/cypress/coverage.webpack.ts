import * as path from 'path';

export default {
  module: {
    rules: [
      {
        test: /\.ts$/,
        include: path.join(__dirname, '..', 'src'),
        exclude: [
          /\.(spec|e2e)\.ts$/,
          /node_modules/,
          /(ngfactory|ngstyle)\.js/,
        ],
        use: {
          loader: '@jsdevtools/coverage-istanbul-loader',
          options: {
            esModules: true,
          },
        },
        enforce: 'post',
      },
    ],
  },
};
