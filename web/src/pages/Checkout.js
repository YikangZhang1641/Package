import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Paper from '@material-ui/core/Paper';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import Button from '@material-ui/core/Button';
import Link from '@material-ui/core/Link';
import Typography from '@material-ui/core/Typography';
import AddressForm from './AddressForm';
import RateForm from './RateForm';
import Review from './Review';

function MadeWithLove() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Built with love by the '}
      <Link color="inherit" href="https://material-ui.com/">
        Material-UI
      </Link>
      {' team.'}
    </Typography>
  );
}

const useStyles = theme => ({
  appBar: {
    position: 'relative',
  },
  layout: {
    width: 'auto',
    marginLeft: theme.spacing(2),
    marginRight: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(2) * 2)]: {
      width: 600,
      marginLeft: 'auto',
      marginRight: 'auto',
    },
  },
  paper: {
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
      marginTop: theme.spacing(6),
      marginBottom: theme.spacing(6),
      padding: theme.spacing(3),
    },
  },
  stepper: {
    padding: theme.spacing(3, 0, 5),
  },
  buttons: {
    display: 'flex',
    justifyContent: 'flex-end',
  },
  button: {
    marginTop: theme.spacing(3),
    marginLeft: theme.spacing(1),
  },
});

const steps = ['Shipping address', 'Show Rates', 'Review your order'];

class Checkout extends React.Component {
  state = {
    activeStep: 0,
  };

  renderContent() {
    const {
      fromAddress,
      toAddress,
      updateAddress,
      selectedOrderID,
      setSelectedOrderID,
    } = this.props;
    const { activeStep } = this.state;
    switch (activeStep) {
      case 0:
        return (
          <AddressForm
            fromAddress={fromAddress}
            toAddress={toAddress}
            updateAddress={updateAddress}
          />
        );
      case 1:
        return (
          <RateForm
            selectedOrderID={selectedOrderID}
            setSelectedOrderID={setSelectedOrderID}
          />
        );
      case 2:
        return <Review />;
      default:
        throw new Error('Unknown step');
    }
  }

  handleNext = () => {
    switch (this.state.activeStep) {
      case 0:
        // TODO: Make API call to get quote
        break;
      case 2:
        // TODO: Make API call to place order
        break;
      default:
    }
    this.setState(prevState => ({ activeStep: prevState.activeStep + 1}));
  }

  handleBack = () => {
    this.setState(prevState => ({ activeStep: prevState.activeStep - 1}));
  }

  render() {
    const {
      classes,
      selectedOrderID,
      setSelectedOrderID,
      fromAddress,
      toAddress,
    } = this.props;
    const { activeStep } = this.state;

    let isNextButtonDisabled = false;
    switch (activeStep) {
      case 0:
        if (
          false
          /*
          fromAddress.addressLine1 === '' ||
          fromAddress.zipCode === '' ||
          fromAddress.city === '' ||
          toAddress.addressLine1 === '' ||
          toAddress.zipCode === '' ||
          toAddress.city === ''
          */
        ) {
          isNextButtonDisabled = true;
        }
        break;
      case 1:
        if (selectedOrderID === null) {
          isNextButtonDisabled = true;
        }
        break;
      default:
    }

    return (
      <React.Fragment>
        <CssBaseline />
        <AppBar position="absolute" color="default" className={classes.appBar}>
          <Toolbar>
            <Typography variant="h6" color="inherit" noWrap>
              Company name
            </Typography>
          </Toolbar>
        </AppBar>
        <main className={classes.layout}>
          <Paper className={classes.paper}>
            <Typography component="h1" variant="h4" align="center">
              Rate & Ship
            </Typography>
            <Stepper activeStep={this.state.activeStep} className={classes.stepper}>
              {steps.map(label => (
                <Step key={label}>
                  <StepLabel>{label}</StepLabel>
                </Step>
              ))}
            </Stepper>
            <React.Fragment>
              {this.state.activeStep === steps.length ? (
                <React.Fragment>
                  <Typography variant="h5" gutterBottom>
                    Thank you for your order.
                  </Typography>
                  <Typography variant="subtitle1">
                    Your order number is #2001539. We have emailed your order confirmation, and will
                    send you an update when your order has shipped.
                  </Typography>
                </React.Fragment>
              ) : (
                <React.Fragment>
                  {this.renderContent()}
                  <div className={classes.buttons}>
                    {this.state.activeStep !== 0 && (
                      <Button onClick={this.handleBack} className={classes.button}>
                        Back
                      </Button>
                    )}
                    <Button
                      disabled={isNextButtonDisabled}
                      variant="contained"
                      color="primary"
                      onClick={this.handleNext}
                      className={classes.button}
                    >
                      {this.state.activeStep === steps.length - 1 ? 'Place order' : 'Next'}
                    </Button>
                  </div>
                </React.Fragment>
              )}
            </React.Fragment>
          </Paper>
          <MadeWithLove />
        </main>
      </React.Fragment>
    );
  }
}

export default withStyles(useStyles)(Checkout);
