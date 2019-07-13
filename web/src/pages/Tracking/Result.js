import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';

const useStyles = makeStyles(theme => ({
  listItem: {
    padding: theme.spacing(0, 0),
  },
  total: {
    fontWeight: '700',
  },
  title: {
    marginTop: theme.spacing(2),
  },
}));

export default function Result(props) {
  const { trackingNumber } = props;
  const infos = [trackingNumber]
  const classes = useStyles();

  return (
    <React.Fragment>
      <Typography variant="h6" gutterBottom>
        Shipping result
      </Typography>
      <List disablePadding>
        {infos.map((info, index) => {
          return (
            <div key={index}>
              <Typography
                variant="subtitle1"
                align="left"
                color="textSecondary"
                component="p"
              >
                {(index === 0 ? 'From' : 'To') + ': ' + info.trackingNumber}
              </Typography>
              <Typography
                variant="subtitle1"
                align="left"
                color="textPrimary"
                component="p"
              >
                {info.addressLine1 + ', ' + info.city + ', ' + info.state}
              </Typography>
            </div>
          )})}
        <ListItem className={classes.listItem}>
          <ListItemText primary="Total" secondary="Estimated Arrival: 2019/06/30 15:00"/>
            <Typography variant="subtitle1" className={classes.total}>
              $15.00
            </Typography>
        </ListItem>
      </List>
    </React.Fragment>
  );
}
