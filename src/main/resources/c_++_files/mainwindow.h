#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QMessageBox>
#include <QFileDialog>
#include <QString>
#include <qcustomplot/qcustomplot.h>

#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc.hpp>

#include <vector>
#include <cmath>
#include <iostream>
#include <fstream>

#include "Image/CapturedImage.h"

namespace Ui {
  class MainWindow;
}

using namespace cv;

typedef struct {
  int r;
  int c;
  int w;
  int h;
} miRoi;


const miRoi roi[] =
{
  { 34,  26,  84,  76  },  // ROI 0
  { 52,  217, 102, 267 },  // ROI 1
  { 160, 200, 210, 250 },  // ROI 2
  { 220, 90,  270, 140 },  // ROI 3
// Sinteticas
  { 50,  100, 150, 200},   // ROI 4
  { 10,  260, 110, 360},   // ROI 5
  { 200, 400, 300, 500},   // ROI 6
  { 340, 200, 440, 300},   // ROI 7
  { 150, 105, 170, 125},   // ROI 8
 //{(esq superior izq), (esq superior der)}
  { 130, 130, 150, 150}    // ROI 9
};


class MainWindow : public QMainWindow {
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    // Process
    CapturedImage* medianFilterHuang(CapturedImage *item, double k, int dist);
    CapturedImage* leeFilter(CapturedImage *item, int radius);
    CapturedImage* leeRobustFilter(CapturedImage *item, int radius/*, int r, int c*/);
    CapturedImage* newLeeRobustFilter(CapturedImage *item, int radius);
    CapturedImage* leeRobustPercentileVersion(CapturedImage *item, int radius, int perInf, int perSup);
    void UnsharpMask(CapturedImage *Original, CapturedImage *&dst);

    // Metrics
    void getMSSIM( CapturedImage *image1, CapturedImage *image2, Scalar &m_luminance, Scalar &m_contrast, Scalar &m_structure);
    double getPSNR(CapturedImage *image1, CapturedImage *image2);
    double getENL(CapturedImage *image);
    double getSSI(CapturedImage *I_original, CapturedImage *I_filtrada);
    double getSMPI(CapturedImage *I_original, CapturedImage *I_filtrada);
    Scalar getMSSIM( CapturedImage *image1, CapturedImage *image2);
    Scalar speackle_reduction_score (CapturedImage *image1, CapturedImage *image2);
    double macana_metric(CapturedImage *I, CapturedImage *K);
    double entropy_metric(CapturedImage *I);
    void show_multiple_histograms();
    void show_histogram(cv::Mat1b const& image);

    void rankify(double *array, double *&rank, int size);

    void TestHistogramImage();
    void TestCorrelationImages();
    void imageToDoubleArray(CapturedImage *image, double **array);
    double getPearsonCoefficient(double **im1, double **im2, int H, int W);

private slots:
    void on_LeeButton_clicked();
    void on_actionInput_Images_Path_triggered();

private:
    Ui::MainWindow *ui;
    std::string path;

    // Auxiliary
    int numberCells(CapturedImage *item, int row, int col, int dist);
    int statistic_order(int *histogram, int nc, int b, double k);
    int statistic_order(int **histogram, int row, int nc, int b, double k);
    CapturedImage *convert24to8bits(IplImage *image);

};

#endif // MAINWINDOW_H
